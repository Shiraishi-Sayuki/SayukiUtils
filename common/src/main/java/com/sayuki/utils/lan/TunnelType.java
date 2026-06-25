package com.sayuki.utils.lan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sayuki.utils.config.ConfigManager;
import com.sayuki.utils.lan.LanFeatureConfig;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.MinecraftServer;

public enum TunnelType {
    NONE("none") {
        @Override @Nullable
        // 開始
        public Component start(MinecraftServer server) { return null; }
        @Override
        // 停止
        public void stop(MinecraftServer server) {}
    },
    NGROK("ngrok") {
        @Override @Nullable
        // 開始
        public Component start(MinecraftServer server) throws TunnelException {
            int port = server.getPort();
            String authtoken = ((LanFeatureConfig) ConfigManager.get().get("lan")).getNgrokAuthtoken();
            Process ngrokProcess = NgrokManager.start(port, authtoken);
            Optional<String> publicUrl = NgrokManager.getPublicUrl(ngrokProcess);
            if (publicUrl.isEmpty()) {
                throw new TunnelException(Component.translatable("sayukiutils.lan.tunnel.ngrok.failed"));
            }
            return Component.translatable("sayukiutils.lan.tunnel.ngrok.started", publicUrl.get())
                    .copy().withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, publicUrl.get()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable("chat.link.open"))));
        }
        @Override
        // 停止
        public void stop(MinecraftServer server) {
            NgrokManager.stop();
        }
    },
    ;

    public static class TunnelException extends Exception {
        private final Component messageText;

        public TunnelException(Component messageText) {
            super(messageText.getString());
            this.messageText = messageText;
        }

        public TunnelException(Component messageText, Throwable cause) {
            super(messageText.getString(), cause);
            this.messageText = messageText;
        }

        // メッセージテキストを取得
        public Component getMessageText() {
            return this.messageText;
        }
    }

    private final String name;

    // 名前から取得
    public static TunnelType byName(String name) {
        return byName(name, NONE);
    }

    @Nullable
    // 名前から取得
    public static TunnelType byName(String name, @Nullable TunnelType defaultTunnelType) {
        for (TunnelType t : values()) {
            if (t.name.equals(name)) return t;
        }
        return defaultTunnelType;
    }

    TunnelType(String name) {
        this.name = name;
    }

    @Nullable
    // 開始
    public abstract Component start(MinecraftServer server) throws TunnelException;

    // 停止
    public abstract void stop(MinecraftServer server) throws TunnelException;

    // 名前を取得
    public String getName() {
        return this.name;
    }

    // 翻訳可能な名前を取得
    public Component getTranslatableName() {
        return Component.translatable("sayukiutils.lan.tunnel." + this.name);
    }

    // 文字列として取得
    public String asString() {
        return this.name;
    }

    private static class NgrokManager {
        private static final Logger LOGGER = LoggerFactory.getLogger("sayukiutils-ngrok");
        private static final int API_PORT = 4040;
        private static Process ngrokProcess;

        // 開始
        static Process start(int tunnelPort, String authtoken) throws TunnelException {
            stop();

            if (!isNgrokInstalled()) {
                throw new TunnelException(
                        Component.translatable("sayukiutils.lan.tunnel.ngrok.notFound"));
            }

            try {
                if (!authtoken.isEmpty()) {
                    int exitCode = new ProcessBuilder("ngrok", "config", "add-authtoken", authtoken)
                            .inheritIO().start().waitFor();
                    if (exitCode != 0) {
                        LOGGER.warn("ngrok config add-authtoken exited with code {}", exitCode);
                    }
                }

                ngrokProcess = new ProcessBuilder("ngrok", "tcp", String.valueOf(tunnelPort),
                        "--log", "stdout", "--log-format", "json")
                        .redirectErrorStream(true).start();

                return ngrokProcess;
            } catch (Exception e) {
                LOGGER.error("Failed to start ngrok process", e);
                ngrokProcess = null;
                throw new TunnelException(
                        Component.translatable("sayukiutils.lan.tunnel.ngrok.error", e.getMessage()), e);
            }
        }

        // ngrokがインストールされているか確認
        private static boolean isNgrokInstalled() {
            try {
                Process process = new ProcessBuilder("ngrok", "version")
                        .redirectErrorStream(true).start();
                boolean ok = process.waitFor() == 0;
                process.destroyForcibly();
                return ok;
            } catch (Exception e) {
                return false;
            }
        }

        // 公開URLを取得
        static Optional<String> getPublicUrl(Process process) {
            long start = System.currentTimeMillis();
            int timeoutMs = 15000;
            String tunnelUrl = null;

            while (System.currentTimeMillis() - start < timeoutMs) {
                try {
                    Thread.sleep(500);

                    URI apiUri = URI.create("http://127.0.0.1:" + API_PORT + "/api/tunnels");
                    HttpURLConnection conn = (HttpURLConnection) apiUri.toURL().openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(1000);
                    conn.setReadTimeout(1000);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            tunnelUrl = parseTunnelUrl(response.toString());
                            if (tunnelUrl != null && !tunnelUrl.isEmpty()) {
                                return Optional.of(tunnelUrl);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            return Optional.empty();
        }

        // トンネルURLを解析
        private static String parseTunnelUrl(String json) {
            try {
                int tunnelsIdx = json.indexOf("\"tunnels\":");
                if (tunnelsIdx < 0) return null;

                int arrStart = json.indexOf('[', tunnelsIdx);
                if (arrStart < 0) return null;

                int publicUrlIdx = json.indexOf("\"public_url\"", arrStart);
                if (publicUrlIdx < 0) return null;

                int colonIdx = json.indexOf(':', publicUrlIdx);
                int quoteStart = json.indexOf('"', colonIdx + 1);
                int quoteEnd = json.indexOf('"', quoteStart + 1);
                if (quoteStart < 0 || quoteEnd < 0) return null;

                return json.substring(quoteStart + 1, quoteEnd);
            } catch (Exception e) {
                return null;
            }
        }

        // 停止
        static void stop() {
            if (ngrokProcess != null) {
                ngrokProcess.destroyForcibly();
                ngrokProcess = null;
            }
        }
    }
}

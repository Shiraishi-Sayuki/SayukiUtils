package com.sayuki.utils.lan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.sayuki.utils.lan.TunnelType.TunnelException;
import com.sayuki.utils.lan.mixin.IntegratedServerAccessor;
import com.sayuki.utils.lan.mixin.PlayerListAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.server.LanServerPinger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class LanServer {
    private static final String PUBLISH_STARTED_LAN_TEXT = "commands.publish.started";
    private static final String PUBLISH_PORT_CHANGE_FAILED_TEXT = "commands.publish.failed.port_change";
    private static final String PUBLISH_SAVED_TEXT = "commands.publish.saved";
    private static final Component SERVER_STOPPED_TEXT = Component.translatable("multiplayer.disconnect.server_shutdown");

    // MOTDを処理
    public static String processMotd(MinecraftServer server, String rawMotd) {
        String motd = rawMotd
                .replace("${username}", server.getSingleplayerProfile().getName())
                .replace("${world}", server.getWorldData().getLevelName());
        return motd.replaceAll("((?:[^&]|^)(?:&&)*)&(?!&)", "$1\u00a7").replace("&&", "&");
    }

    // LANサーバーを開始または保存
    public static void startOrSaveLan(MinecraftServer server, GameType gameType, boolean onlineMode, boolean pvpEnabled,
            TunnelType tunnel, int port, int maxPlayers, String rawMotd, Consumer<Component> onSuccess,
            Runnable onFatalError, Consumer<Component> onNonFatalError) {
        LanServerValues serverValues = (LanServerValues) server;
        PlayerList playerList = server.getPlayerList();

        server.setUsesAuthentication(onlineMode);
        server.setPvpAllowed(pvpEnabled);

        ((PlayerListAccessor) playerList).setMaxPlayers(maxPlayers);

        String oldMotd = server.getMotd();
        String motd = processMotd(server, rawMotd);
        serverValues.setRawMotd(rawMotd);
        server.setMotd(motd);

        if (isPublished(server)) {
            int oldPort = server.getPort();
            boolean portChanged = false;
            if (port != oldPort) {
                try {
                    server.getConnection().startTcpServerListener(null, port);
                    server.getConnection().stop();
                    server.getConnection().startTcpServerListener(null, port);
                    ((IntegratedServerAccessor) server).setPublishedPort(port);
                    portChanged = true;
                } catch (IOException e) {
                    onNonFatalError.accept(Component.translatable(PUBLISH_PORT_CHANGE_FAILED_TEXT,
                            ComponentUtils.copyOnClickText(String.valueOf(oldPort))));
                }
            }

            if (portChanged || !motd.equals(oldMotd)) {
                ((IntegratedServerAccessor) server).getLanPinger().interrupt();
                try {
                    LanServerPinger lanPinger = new LanServerPinger(motd, Integer.toString(server.getPort()));
                    ((IntegratedServerAccessor) server).setLanPinger(lanPinger);
                    lanPinger.start();
                } catch (IOException e) {
                }
            }

            server.setDefaultGameType(gameType);
            for (ServerPlayer player : playerList.getPlayers()) {
                playerList.sendPlayerPermissionLevel(player);
            }

            TunnelType oldTunnel = serverValues.getTunnelType();
            if (tunnel != oldTunnel || portChanged) {
                try {
                    oldTunnel.stop(server);
                } catch (TunnelException e) {
                    onNonFatalError.accept(e.getMessageText());
                }

                try {
                    Component tunnelText = tunnel.start(server);
                    serverValues.setTunnelType(tunnel);
                    serverValues.setTunnelText(tunnelText);
                    onSuccess.accept(Component.translatable(PUBLISH_SAVED_TEXT,
                            ComponentUtils.copyOnClickText(String.valueOf(server.getPort())), motd));
                    if (tunnelText != null) onSuccess.accept(tunnelText);
                } catch (TunnelException e) {
                    onSuccess.accept(Component.translatable(PUBLISH_SAVED_TEXT,
                            ComponentUtils.copyOnClickText(String.valueOf(server.getPort())), motd));
                    onNonFatalError.accept(e.getMessageText());
                }
            } else {
                onSuccess.accept(Component.translatable(PUBLISH_SAVED_TEXT,
                        ComponentUtils.copyOnClickText(String.valueOf(server.getPort())), motd));
            }
        } else {
            if (server.publishServer(gameType, false, port)) {
                server.setDefaultGameType(gameType);

                try {
                    Component tunnelText = tunnel.start(server);
                    serverValues.setTunnelType(tunnel);
                    serverValues.setTunnelText(tunnelText);
                    onSuccess.accept(Component.translatable(PUBLISH_STARTED_LAN_TEXT,
                            ComponentUtils.copyOnClickText(String.valueOf(server.getPort())), motd));
                    if (tunnelText != null) onSuccess.accept(tunnelText);
                } catch (TunnelException e) {
                    onSuccess.accept(Component.translatable(PUBLISH_STARTED_LAN_TEXT,
                            ComponentUtils.copyOnClickText(String.valueOf(server.getPort())), motd));
                    onNonFatalError.accept(e.getMessageText());
                }
            } else {
                onFatalError.run();
            }
            Minecraft.getInstance().updateTitle();
        }
    }

    // LANサーバーを停止
    public static void stopLan(MinecraftServer server, Runnable onSuccess,
            Runnable onFatalError, Consumer<Component> onNonFatalError) {
        LanServerValues serverValues = (LanServerValues) server;

        UUID localPlayerUuid = ((IntegratedServerAccessor) server).getLocalPlayerUuid();
        PlayerList playerList = server.getPlayerList();
        List<ServerPlayer> playerListCopy = new ArrayList<>(playerList.getPlayers());
        for (ServerPlayer player : playerListCopy) {
            if (!player.getUUID().equals(localPlayerUuid)) {
                player.connection.disconnect(SERVER_STOPPED_TEXT);
            }
        }

        server.getConnection().stop();
        ((IntegratedServerAccessor) server).setPublishedPort(-1);
        ((IntegratedServerAccessor) server).getLanPinger().interrupt();

        onSuccess.run();

        try {
            serverValues.getTunnelType().stop(server);
            serverValues.setTunnelType(TunnelType.NONE);
            serverValues.setTunnelText(null);
        } catch (TunnelException e) {
            onNonFatalError.accept(e.getMessageText());
        }

        Minecraft.getInstance().updateTitle();
    }

    // 公開中かどうかを確認
    private static boolean isPublished(MinecraftServer server) {
        if (server instanceof IntegratedServer integrated) {
            return integrated.isPublished();
        }
        return false;
    }
}

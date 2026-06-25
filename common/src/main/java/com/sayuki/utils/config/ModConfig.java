package com.sayuki.utils.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sayuki.utils.darkmode.DarkModeFeatureConfig;
import com.sayuki.utils.eatanim.EatingAnimationFeatureConfig;
import com.sayuki.utils.lan.LanFeatureConfig;
import com.sayuki.utils.playtime.PlayTimeFeatureConfig;

public class ModConfig {
    private final Map<String, FeatureConfig> features = new LinkedHashMap<>();

    // フィーチャー設定を初期化するよ
    public ModConfig() {
        register(new CopyUUIDFeatureConfig());
        register(new DarkModeFeatureConfig());
        register(new EatingAnimationFeatureConfig());
        register(new PlayTimeFeatureConfig());
        register(new LanFeatureConfig());
    }

    // フィーチャーを登録するよ
    public void register(FeatureConfig config) {
        features.put(config.getFeatureId(), config);
    }

    @SuppressWarnings("unchecked")
    // フィーチャーを取得するよ
    public <T extends FeatureConfig> T get(String featureId) {
        return (T) features.get(featureId);
    }

    // 全フィーチャーを取得するよ
    public Map<String, FeatureConfig> getAll() {
        return features;
    }

    // 全設定をファイルに保存するよ
    public void saveAll(Path dir) {
        try {
            Files.createDirectories(dir);
            // save main sayukiutils.json with feature list + enabled toggles
            JsonObject main = new JsonObject();
            JsonObject feats = new JsonObject();
            for (FeatureConfig fc : features.values()) {
                feats.addProperty(fc.getFeatureId(), fc.isEnabled());
            }
            main.add("features", feats);
            writeJson(dir.resolve("sayukiutils.json"), main);

            // save each feature to its own file
            for (FeatureConfig fc : features.values()) {
                if (fc.hasIndividualFile()) {
                    writeJson(dir.resolve(fc.getFeatureId() + ".json"), fc.serialize());
                }
            }
        } catch (Exception e) {
        }
    }

    // 全設定をファイルから読み込むよ
    public void loadAll(Path dir) {
        try {
            // load main sayukiutils.json
            Path mainFile = dir.resolve("sayukiutils.json");
            if (Files.exists(mainFile)) {
                try (BufferedReader reader = Files.newBufferedReader(mainFile, StandardCharsets.UTF_8)) {
                    JsonObject main = JsonParser.parseReader(reader).getAsJsonObject();
                    if (main.has("features")) {
                        JsonObject feats = main.getAsJsonObject("features");
                        for (Map.Entry<String, JsonElement> entry : feats.entrySet()) {
                            if (entry.getValue().isJsonPrimitive()) {
                                FeatureConfig fc = features.get(entry.getKey());
                                if (fc != null) {
                                    fc.setEnabled(entry.getValue().getAsBoolean());
                                }
                            }
                        }
                    }
                }
            }

            // load each feature from its own file
            for (FeatureConfig fc : features.values()) {
                if (!fc.hasIndividualFile()) continue;
                Path featFile = dir.resolve(fc.getFeatureId() + ".json");
                if (Files.exists(featFile)) {
                    try (BufferedReader reader = Files.newBufferedReader(featFile, StandardCharsets.UTF_8)) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        fc.deserialize(json);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    // JSONファイルに書き込むよ
    private void writeJson(Path path, JsonObject json) throws IOException {
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
        }
    }
}

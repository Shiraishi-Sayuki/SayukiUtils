/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.config;

import java.nio.file.Path;

public class ConfigManager {
    private static ModConfig config;
    private static Path configDir;

    // ModConfigを取得するよ（遅延初期化）
    public static ModConfig get() {
        if (config == null) {
            config = new ModConfig();
        }
        return config;
    }

    // 設定マネージャーを初期化するよ
    public static void init(Path dir) {
        configDir = dir;
        config = new ModConfig();
        config.loadAll(configDir);
        config.saveAll(configDir);
    }

    // 設定を再読み込みするよ
    public static void load() {
        if (configDir == null || config == null) return;
        config.loadAll(configDir);
    }

    // 設定を保存するよ
    public static void save() {
        if (configDir == null || config == null) return;
        config.saveAll(configDir);
    }

    // ConfigDirを取得
    public static Path getConfigDir() {
        return configDir;
    }
}

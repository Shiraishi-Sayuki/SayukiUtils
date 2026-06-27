/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils;

import java.nio.file.Path;

import com.sayuki.utils.config.ConfigManager;


public class SayukiUtils {
    public static final String MOD_ID = "sayukiutils";

    // 初期化
    public static void init(Path configDir) {
        ConfigManager.init(configDir.resolve("sayukiutils"));
    }
}

/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.darkmode;

import java.util.HashSet;
import java.util.Set;

public class RenderedClassesTracker {

    private static final Set<String> TRACKED = new HashSet<>();

    // トラッキングスレッドを開始
    public static void start(){
        new Thread(() -> {
            while (true) {
                if (isDumpEnabled() && !TRACKED.isEmpty()){
                    TRACKED.clear();
                }
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    // ダンプが有効かチェック
    private static boolean isDumpEnabled() {
        var cfg = com.sayuki.utils.config.ConfigManager.get().get("darkmode");
        return cfg instanceof DarkModeFeatureConfig dmcfg && dmcfg.isMethodShaderDump();
    }

    // レンダリングされたクラス名を追加
    public static void add(String element){
        if (isDumpEnabled()) TRACKED.add(element);
    }

}

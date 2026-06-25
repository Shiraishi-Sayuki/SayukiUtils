package com.sayuki.utils.fabric.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import com.sayuki.utils.client.config.ConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    // 設定画面ファクトリーを取得
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::create;
    }
}

/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.eatanim;

import com.google.gson.JsonObject;

import com.sayuki.utils.config.FeatureConfig;

public class EatingAnimationFeatureConfig extends FeatureConfig {
    // EatingAnimationFeatureConfigを作成
    public EatingAnimationFeatureConfig() {
        super("eatanim");
    }

    @Override
    // 個別ファイルなし
    public boolean hasIndividualFile() { return false; }

    @Override
    // JsonObjectにシリアライズ
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        serializeBase(json);
        return json;
    }

    @Override
    // JsonObjectからデシリアライズ
    public void deserialize(JsonObject json) {
        deserializeBase(json);
    }
}

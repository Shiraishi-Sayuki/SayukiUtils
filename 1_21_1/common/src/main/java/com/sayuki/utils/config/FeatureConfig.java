/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.config;

import com.google.gson.JsonObject;

public abstract class FeatureConfig {
    private boolean enabled = true;
    private final String featureId;

    // フィーチャーIDを設定するよ
    public FeatureConfig(String featureId) {
        this.featureId = featureId;
    }

    // フィーチャーIDを返すよ
    public String getFeatureId() { return featureId; }
    // 有効かどうかを返すよ
    public boolean isEnabled() { return enabled; }
    // 有効/無効を設定するよ
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // 個別ファイルを持つかチェックするよ
    public boolean hasIndividualFile() { return true; }

    // シリアライズするよ（abstract）
    public abstract JsonObject serialize();
    // デシリアライズするよ（abstract）
    public abstract void deserialize(JsonObject json);

    // ベース部分をシリアライズするよ
    protected void serializeBase(JsonObject json) {
        json.addProperty("enabled", enabled);
    }

    // ベース部分をデシリアライズするよ
    protected void deserializeBase(JsonObject json) {
        if (json.has("enabled")) enabled = json.get("enabled").getAsBoolean();
    }
}

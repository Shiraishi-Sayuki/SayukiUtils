package com.sayuki.utils.config;

import com.google.gson.JsonObject;

public class CopyUUIDFeatureConfig extends FeatureConfig {
    private String triggerItem = "minecraft:stick";

    // コンストラクタ〜copyuuid機能の設定だよ
    public CopyUUIDFeatureConfig() {
        super("copyuuid");
    }

    // トリガーアイテムを取得するよ
    public String getTriggerItem() { return triggerItem; }
    // トリガーアイテムを設定するよ
    public void setTriggerItem(String v) { this.triggerItem = v; }

    @Override
    // 設定をJSONにシリアライズするよ
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        serializeBase(json);
        json.addProperty("triggerItem", triggerItem);
        return json;
    }

    @Override
    // JSONから設定をデシリアライズするよ
    public void deserialize(JsonObject json) {
        deserializeBase(json);
        if (json.has("triggerItem")) triggerItem = json.get("triggerItem").getAsString();
    }
}

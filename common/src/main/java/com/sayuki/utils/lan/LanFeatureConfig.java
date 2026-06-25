package com.sayuki.utils.lan;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.sayuki.utils.config.FeatureConfig;

public class LanFeatureConfig extends FeatureConfig {
    private String ngrokAuthtoken = "";
    @Nullable
    private LanSettings globalLanSettings;
    public LanFeatureConfig() {
        super("lan");
    }

    // ngrok認証トークンを取得
    public String getNgrokAuthtoken() { return ngrokAuthtoken; }
    // ngrok認証トークンを設定
    public void setNgrokAuthtoken(String v) { this.ngrokAuthtoken = v; }

    @Nullable
    // グローバルLAN設定を取得
    public LanSettings getGlobalLanSettings() { return globalLanSettings; }
    // グローバルLAN設定を設定
    public void setGlobalLanSettings(@Nullable LanSettings v) { this.globalLanSettings = v; }

    @Override
    // シリアライズ
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        serializeBase(json);
        json.addProperty("ngrokAuthtoken", ngrokAuthtoken);
        if (globalLanSettings != null) {
            json.add("globalLanSettings", globalLanSettings.toJson());
        }
        return json;
    }

    @Override
    // デシリアライズ
    public void deserialize(JsonObject json) {
        deserializeBase(json);
        if (json.has("ngrokAuthtoken")) ngrokAuthtoken = json.get("ngrokAuthtoken").getAsString();
        if (json.has("globalLanSettings")) {
            globalLanSettings = LanSettings.fromJson(json.getAsJsonObject("globalLanSettings"));
        }
    }
}

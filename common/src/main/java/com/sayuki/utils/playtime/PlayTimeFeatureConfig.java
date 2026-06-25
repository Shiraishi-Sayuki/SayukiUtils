package com.sayuki.utils.playtime;

import com.google.gson.JsonObject;
import com.sayuki.utils.config.FeatureConfig;
import com.sayuki.utils.playtime.util.Color;
import com.sayuki.utils.playtime.util.ServerEntryRenderPos;
import com.sayuki.utils.playtime.util.WorldEntryRenderPos;

public class PlayTimeFeatureConfig extends FeatureConfig {
    private boolean showServerPlayTime;
    private boolean showWorldPlayTime = true;
    private ServerEntryRenderPos serverPlayTimePosition = ServerEntryRenderPos.AFTER_NAME;
    private WorldEntryRenderPos worldPlayTimePosition = WorldEntryRenderPos.TOP_RIGHT;
    private Color serverPlayTimeColor = new Color(128, 128, 128, 255);
    private Color worldPlayTimeColor = new Color(128, 128, 128, 255);

    public PlayTimeFeatureConfig() {
        super("playtime");
    }

    // サーバープレイ時間表示が有効か確認
    public boolean isShowServerPlayTime() { return showServerPlayTime; }
    // サーバープレイ時間表示を設定
    public void setShowServerPlayTime(boolean v) { this.showServerPlayTime = v; }
    // ワールドプレイ時間表示が有効か確認
    public boolean isShowWorldPlayTime() { return showWorldPlayTime; }
    // ワールドプレイ時間表示を設定
    public void setShowWorldPlayTime(boolean v) { this.showWorldPlayTime = v; }
    // サーバープレイ時間表示位置を取得
    public ServerEntryRenderPos getServerPlayTimePosition() { return serverPlayTimePosition; }
    // サーバープレイ時間表示位置を設定
    public void setServerPlayTimePosition(ServerEntryRenderPos v) { this.serverPlayTimePosition = v; }
    // ワールドプレイ時間表示位置を取得
    public WorldEntryRenderPos getWorldPlayTimePosition() { return worldPlayTimePosition; }
    // ワールドプレイ時間表示位置を設定
    public void setWorldPlayTimePosition(WorldEntryRenderPos v) { this.worldPlayTimePosition = v; }
    // サーバープレイ時間色を取得
    public Color getServerPlayTimeColor() { return serverPlayTimeColor; }
    // サーバープレイ時間色を設定
    public void setServerPlayTimeColor(Color v) { this.serverPlayTimeColor = v; }
    // ワールドプレイ時間色を取得
    public Color getWorldPlayTimeColor() { return worldPlayTimeColor; }
    // ワールドプレイ時間色を設定
    public void setWorldPlayTimeColor(Color v) { this.worldPlayTimeColor = v; }
    // ARGBからサーバープレイ時間色を設定
    public void setServerPlayTimeColorFromARGB(int argb) { this.serverPlayTimeColor = Color.fromARGB(argb); }
    // ARGBからワールドプレイ時間色を設定
    public void setWorldPlayTimeColorFromARGB(int argb) { this.worldPlayTimeColor = Color.fromARGB(argb); }

    @Override
    // シリアライズ
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        serializeBase(json);
        json.addProperty("showServerPlayTime", showServerPlayTime);
        json.addProperty("showWorldPlayTime", showWorldPlayTime);
        json.addProperty("serverPlayTimePosition", serverPlayTimePosition.name().toLowerCase());
        json.addProperty("worldPlayTimePosition", worldPlayTimePosition.name().toLowerCase());
        json.addProperty("serverPlayTimeColor", serverPlayTimeColor.toString());
        json.addProperty("worldPlayTimeColor", worldPlayTimeColor.toString());
        return json;
    }

    @Override
    // デシリアライズ
    public void deserialize(JsonObject json) {
        deserializeBase(json);
        if (json.has("showServerPlayTime")) showServerPlayTime = json.get("showServerPlayTime").getAsBoolean();
        if (json.has("showWorldPlayTime")) showWorldPlayTime = json.get("showWorldPlayTime").getAsBoolean();
        if (json.has("serverPlayTimePosition")) {
            for (ServerEntryRenderPos p : ServerEntryRenderPos.values()) {
                if (p.name().equalsIgnoreCase(json.get("serverPlayTimePosition").getAsString())) {
                    serverPlayTimePosition = p; break;
                }
            }
        }
        if (json.has("worldPlayTimePosition")) {
            for (WorldEntryRenderPos p : WorldEntryRenderPos.values()) {
                if (p.name().equalsIgnoreCase(json.get("worldPlayTimePosition").getAsString())) {
                    worldPlayTimePosition = p; break;
                }
            }
        }
        if (json.has("serverPlayTimeColor")) {
            Color c = Color.fromString(json.get("serverPlayTimeColor").getAsString());
            if (c != null) serverPlayTimeColor = c;
        }
        if (json.has("worldPlayTimeColor")) {
            Color c = Color.fromString(json.get("worldPlayTimeColor").getAsString());
            if (c != null) worldPlayTimeColor = c;
        }
    }
}

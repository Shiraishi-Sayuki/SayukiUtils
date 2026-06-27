/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.darkmode;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.sayuki.utils.config.FeatureConfig;

public class DarkModeFeatureConfig extends FeatureConfig {
    private List<String> methodShaderBlacklist = new ArrayList<>(getDefaultBlacklist());
    private boolean methodShaderDump = false;
    private boolean showButtonInTitleScreen = true;
    private int guiButtonX = 32;
    private int guiButtonY = 2;
    private int titleScreenButtonX = 4;
    private int titleScreenButtonY = 40;

    // ダークモード設定を初期化
    public DarkModeFeatureConfig() {
        super("darkmode");
    }

    // ブラックリストを取得
    public List<String> getMethodShaderBlacklist() { return methodShaderBlacklist; }
    // ブラックリストを設定
    public void setMethodShaderBlacklist(List<String> v) { this.methodShaderBlacklist = v; }
    // デバッグダンプ有効？
    public boolean isMethodShaderDump() { return methodShaderDump; }
    // デバッグダンプを設定
    public void setMethodShaderDump(boolean v) { this.methodShaderDump = v; }
    // タイトル画面にボタン表示？
    public boolean isShowButtonInTitleScreen() { return showButtonInTitleScreen; }
    // タイトル画面ボタン表示を設定
    public void setShowButtonInTitleScreen(boolean v) { this.showButtonInTitleScreen = v; }
    // GUIボタンのX座標を取得
    public int getGuiButtonX() { return guiButtonX; }
    // GUIボタンのX座標を設定
    public void setGuiButtonX(int v) { this.guiButtonX = v; }
    // GUIボタンのY座標を取得
    public int getGuiButtonY() { return guiButtonY; }
    // GUIボタンのY座標を設定
    public void setGuiButtonY(int v) { this.guiButtonY = v; }
    // タイトル画面ボタンのXを取得
    public int getTitleScreenButtonX() { return titleScreenButtonX; }
    // タイトル画面ボタンのXを設定
    public void setTitleScreenButtonX(int v) { this.titleScreenButtonX = v; }
    // タイトル画面ボタンのYを取得
    public int getTitleScreenButtonY() { return titleScreenButtonY; }
    // タイトル画面ボタンのYを設定
    public void setTitleScreenButtonY(int v) { this.titleScreenButtonY = v; }

    @Override
    // JSONにシリアライズ
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        serializeBase(json);
        JsonArray list = new JsonArray();
        for (String s : methodShaderBlacklist) list.add(s);
        json.add("methodShaderBlacklist", list);
        json.addProperty("methodShaderDump", methodShaderDump);
        json.addProperty("showButtonInTitleScreen", showButtonInTitleScreen);
        json.addProperty("guiButtonX", guiButtonX);
        json.addProperty("guiButtonY", guiButtonY);
        json.addProperty("titleScreenButtonX", titleScreenButtonX);
        json.addProperty("titleScreenButtonY", titleScreenButtonY);
        return json;
    }

    @Override
    // JSONからデシリアライズ
    public void deserialize(JsonObject json) {
        deserializeBase(json);
        if (json.has("methodShaderBlacklist")) {
            methodShaderBlacklist = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray("methodShaderBlacklist")) {
                methodShaderBlacklist.add(e.getAsString());
            }
        }
        if (json.has("methodShaderDump")) methodShaderDump = json.get("methodShaderDump").getAsBoolean();
        if (json.has("showButtonInTitleScreen")) showButtonInTitleScreen = json.get("showButtonInTitleScreen").getAsBoolean();
        if (json.has("guiButtonX")) guiButtonX = json.get("guiButtonX").getAsInt();
        if (json.has("guiButtonY")) guiButtonY = json.get("guiButtonY").getAsInt();
        if (json.has("titleScreenButtonX")) titleScreenButtonX = json.get("titleScreenButtonX").getAsInt();
        if (json.has("titleScreenButtonY")) titleScreenButtonY = json.get("titleScreenButtonY").getAsInt();
    }

    // デフォルトのブラックリストを取得
    private static List<String> getDefaultBlacklist() {
        return new ArrayList<>(java.util.Arrays.asList(
                "mezz.jei.common.render.FluidTankRenderer:drawTextureWithMasking",
                "mezz.jei.library.render.FluidTankRenderer:drawTextureWithMasking",
                "renderCrosshair",
                "net.minecraft.client.gui.screens.TitleScreen",
                "renderSky",
                "renderHotbar",
                "setupOverlayRenderState",
                "net.minecraftforge.client.gui.overlay.ForgeGui",
                "renderFood",
                "squeek.appleskin.client.HUDOverlayHandler",
                "renderExperienceBar",
                "OnlineServerEntry:drawIcon",
                "WorldSelectionList$WorldListEntry:render",
                "CubeMap:render"
        ));
    }
}

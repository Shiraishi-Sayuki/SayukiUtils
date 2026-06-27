/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.darkmode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;

public class ShaderConfig {

    private List<ShaderValue> shaders;
    private int version;
    private int selectedShaderIndex;
    private static File configFilePath;

    // デフォルトのシェーダー設定を作成
    public ShaderConfig() {
        this.shaders = new ArrayList<>();
        this.version = 2;
        ResourceLocation tex_shader_location = ResourceLocation.fromNamespaceAndPath("sayukiutils", "dark_position_tex");
        ResourceLocation tex_color_shader_location = ResourceLocation.fromNamespaceAndPath("sayukiutils", "dark_position_tex_color");
        this.shaders.add(null);
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, "gui.sayukiutils.darkmode.perfect_dark", (float)5.5, 0xFFFFFF));
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, "gui.sayukiutils.darkmode.less_perfect_dark", (float)3.5, 0xFFFFFF));
        this.shaders.add(new ShaderValue(tex_shader_location, tex_color_shader_location, "gui.sayukiutils.darkmode.toasted_light", (float)2, 0xFFFFFF));
        this.selectedShaderIndex = 0;
    }

    // 設定ファイルのパスを初期化
    public static void init(java.nio.file.Path configDir) {
        configFilePath = configDir.resolve("darkmode-shaders.json").toFile();
        File parent = configFilePath.getParentFile();
        if (parent != null) parent.mkdirs();
    }

    // シェーダー一覧を取得
    public List<ShaderValue> getShaders() {
        if (shaders == null) shaders = new ArrayList<>();
        return shaders;
    }

    // 選択中のシェーダーインデックスをセット
    public void setSelectedShaderIndex(int index) {
        selectedShaderIndex = index;
        createDefaultConfigFile();
    }

    // 選択中のシェーダーインデックスを取得
    public int getSelectedShaderIndex() {
        return selectedShaderIndex;
    }

    // Gsonインスタンスを作成
    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocationSerializer())
                .create();
    }

    private static class ResourceLocationSerializer implements JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {
        @Override
        public JsonElement serialize(ResourceLocation src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public ResourceLocation deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ResourceLocation.parse(json.getAsString());
        }
    }

    // 設定ファイルを読み込む
    public static void load(){
        if (configFilePath == null) return;
        if (!configFilePath.exists()){
            createDefaultConfigFile();
        }
        Gson gson = createGson();
        try (FileReader reader = new FileReader(configFilePath)) {
            SayukiDarkModeClient.CONFIG = gson.fromJson(reader, ShaderConfig.class);
            if (SayukiDarkModeClient.CONFIG == null || SayukiDarkModeClient.CONFIG.version != new ShaderConfig().version) {
                throw new Exception("Invalid config version.");
            }
        } catch (Exception e) {
            SayukiDarkModeClient.CONFIG = new ShaderConfig();
            createDefaultConfigFile();
        }
    }

    // デフォルト設定ファイルを作成
    private static void createDefaultConfigFile(){
        if (configFilePath == null) return;
        File parent = configFilePath.getParentFile();
        if (parent != null) parent.mkdirs();
        Gson gson = createGson();
        try (FileWriter fileWriter = new FileWriter(configFilePath)) {
            gson.toJson(SayukiDarkModeClient.CONFIG, fileWriter);
        } catch (IOException e) {
        }
    }

    public static class ShaderValue {
        public ResourceLocation texShaderLocation;
        public ResourceLocation texColorShaderLocation;
        public String displayName;
        public float divideFactor;
        public int darkColorReplacement;

        // 空のShaderValueを作成
        public ShaderValue() {}

        // ShaderValueを初期化
        public ShaderValue(ResourceLocation texShaderLocation, ResourceLocation texColorShaderLocation, String displayName, float divideFactor, int darkColorReplacement) {
            this.texShaderLocation = texShaderLocation;
            this.texColorShaderLocation = texColorShaderLocation;
            this.displayName = displayName;
            this.divideFactor = divideFactor;
            this.darkColorReplacement = darkColorReplacement;
        }
    }
}

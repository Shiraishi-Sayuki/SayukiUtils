/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.darkmode;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.sayuki.utils.darkmode.mixins.ShaderInstanceAccessor;

public class SayukiDarkModeClient {
    public static Object2BooleanMap<String> BLACKLISTED_ELEMENTS = new Object2BooleanOpenHashMap<>();
    public static List<String> MODDED_BLACKLIST = new ArrayList<>();
    public static ShaderConfig CONFIG = new ShaderConfig();
    public static Map<ResourceLocation, ShaderInstance> TEX_SHADERS = new HashMap<>();
    public static Map<ResourceLocation, ShaderInstance> TEX_COLOR_SHADERS = new HashMap<>();
    public static List<ShaderConfig.ShaderValue> SHADER_VALUES = new ArrayList<>();
    public static ShaderConfig.ShaderValue SELECTED_SHADER_VALUE = null;
    private static Map<ResourceLocation, List<Consumer<ShaderInstance>>> PENDING_CALLBACKS = new HashMap<>();
    private static Map<ResourceLocation, ShaderInstance> RESOLVED_SHADERS = new HashMap<>();

    // ダークモードを初期化するよ
    public static void init(Path configDir) {
        ShaderConfig.init(configDir);
        ShaderConfig.load();
    }

    // 全シェーダーを登録するよ
    public static void registerAllShaders(ShaderRegistrar registrar) {
        SHADER_VALUES.clear();
        TEX_SHADERS.clear();
        TEX_COLOR_SHADERS.clear();
        PENDING_CALLBACKS.clear();
        RESOLVED_SHADERS.clear();

        List<ShaderConfig.ShaderValue> shaders = CONFIG.getShaders();
        int idx = CONFIG.getSelectedShaderIndex();
        SELECTED_SHADER_VALUE = shaders.isEmpty() || idx < 0 || idx >= shaders.size() ? null : shaders.get(idx);

        for (ShaderConfig.ShaderValue shaderValue : shaders) {
            SHADER_VALUES.add(shaderValue);
            if (shaderValue == null) continue;
            registerShaderDeduplicated(registrar, shaderValue.texShaderLocation, DefaultVertexFormat.POSITION_TEX,
                    instance -> {
                        TEX_SHADERS.put(shaderValue.texShaderLocation, instance);
                        applyDivideFactor(instance);
                    });
            registerShaderDeduplicated(registrar, shaderValue.texColorShaderLocation, DefaultVertexFormat.POSITION_TEX_COLOR,
                    instance -> {
                        TEX_COLOR_SHADERS.put(shaderValue.texColorShaderLocation, instance);
                        applyDivideFactor(instance);
                    });
        }
        propagateDivideFactor();
        RenderedClassesTracker.start();
    }

    // シェーダーインスタンスにDivideFactorを設定するよ
    private static void applyDivideFactor(ShaderInstance instance) {
        if (SELECTED_SHADER_VALUE == null) return;
        Uniform uniform = ((ShaderInstanceAccessor) instance).invokeGetUniform("DivideFactor");
        if (uniform != null) uniform.set(SELECTED_SHADER_VALUE.divideFactor);
    }

    // 重複なしでシェーダーを登録するよ
    private static void registerShaderDeduplicated(ShaderRegistrar registrar, ResourceLocation location, VertexFormat format, Consumer<ShaderInstance> callback) {
        ShaderInstance resolved = RESOLVED_SHADERS.get(location);
        if (resolved != null) {
            callback.accept(resolved);
            return;
        }
        List<Consumer<ShaderInstance>> pending = PENDING_CALLBACKS.computeIfAbsent(location, k -> new ArrayList<>());
        pending.add(callback);
        if (pending.size() == 1) {
            registrar.registerShader(location, format, instance -> {
                RESOLVED_SHADERS.put(location, instance);
                List<Consumer<ShaderInstance>> callbacks = PENDING_CALLBACKS.get(location);
                if (callbacks != null) {
                    for (Consumer<ShaderInstance> cb : callbacks) {
                        cb.accept(instance);
                    }
                }
            });
        }
    }

    // 登録を確定するよ
    public static void finalizeRegistration() {
        List<ShaderConfig.ShaderValue> shaders = CONFIG.getShaders();
        int idx = CONFIG.getSelectedShaderIndex();
        SELECTED_SHADER_VALUE = shaders.isEmpty() || idx < 0 || idx >= shaders.size() ? null : shaders.get(idx);
        propagateDivideFactor();
    }

    // 現在の選択に合わせて全シェーダーのDivideFactorを更新するよ
    private static void propagateDivideFactor() {
        if (SELECTED_SHADER_VALUE == null) return;
        float factor = SELECTED_SHADER_VALUE.divideFactor;
        for (ShaderInstance instance : TEX_SHADERS.values()) {
            if (instance != null) setDivideFactor(instance, factor);
        }
        for (ShaderInstance instance : TEX_COLOR_SHADERS.values()) {
            if (instance != null) setDivideFactor(instance, factor);
        }
    }

    private static void setDivideFactor(ShaderInstance instance, float factor) {
        Uniform uniform = ((ShaderInstanceAccessor) instance).invokeGetUniform("DivideFactor");
        if (uniform != null) uniform.set(factor);
    }

    // シェーダー登録をリセットするよ
    public static void resetShaderRegistration() {
    }

    // 選択中のTexシェーダーを取得
    public static ShaderInstance getSelectedTexShader() {
        if (SELECTED_SHADER_VALUE == null) return null;
        ShaderInstance instance = TEX_SHADERS.get(SELECTED_SHADER_VALUE.texShaderLocation);
        return instance;
    }

    // 選択中のTexColorシェーダーを取得
    public static ShaderInstance getSelectedTexColorShader() {
        if (SELECTED_SHADER_VALUE == null) return null;
        ShaderInstance instance = TEX_COLOR_SHADERS.get(SELECTED_SHADER_VALUE.texColorShaderLocation);
        return instance;
    }

    // ダークモードがアクティブかチェック
    public static boolean isActive() {
        if (SELECTED_SHADER_VALUE == null) return false;
        var cfg = com.sayuki.utils.config.ConfigManager.get().get("darkmode");
        return cfg == null || cfg.isEnabled();
    }

    // 機能が有効かチェック
    public static boolean isFeatureEnabled() {
        var cfg = com.sayuki.utils.config.ConfigManager.get().get("darkmode");
        return cfg != null && cfg.isEnabled();
    }

    // 選択中のShaderValueを取得
    public static ShaderConfig.ShaderValue getSelectedShaderValue() {
        return SELECTED_SHADER_VALUE;
    }

    // 要素名がブラックリストに入ってるか
    public static boolean isElementNameBlacklisted(String elementName) {
        return BLACKLISTED_ELEMENTS.computeIfAbsent(elementName, (String name) -> {
            RenderedClassesTracker.add(name);
            return blacklistContains(MODDED_BLACKLIST, name) || blacklistContains(getBlacklistConfig(), name);
        });
    }

    // ブラックリスト設定を取得
    private static List<String> getBlacklistConfig() {
        var cfg = com.sayuki.utils.config.ConfigManager.get().get("darkmode");
        if (cfg instanceof DarkModeFeatureConfig dmcfg) {
            return dmcfg.getMethodShaderBlacklist();
        }
        return List.of();
    }

    // ブラックリストに含まれるか判定
    private static boolean blacklistContains(List<? extends String> blacklist, String elementName) {
        return blacklist.stream().anyMatch(elementName::contains);
    }

    // ブラックリストキャッシュをクリア
    public static void clearBlacklistCache() {
        BLACKLISTED_ELEMENTS.clear();
    }

    // 次のシェーダーインデックスを取得
    private static int getNextShaderValueIndex() {
        if (Screen.hasShiftDown()) {
            return 0;
        }
        List<ShaderConfig.ShaderValue> shaders = CONFIG.getShaders();
        if (shaders.isEmpty()) return 0;
        int nextShaderIndex = CONFIG.getSelectedShaderIndex() + 1;
        if (nextShaderIndex < 0 || nextShaderIndex >= shaders.size()) {
            return 0;
        }
        return nextShaderIndex;
    }

    // 現在のモード名を取得
    private static Component getCurrentModeName() {
        return SELECTED_SHADER_VALUE == null
                ? Component.translatable("gui.sayukiutils.darkmode.light_mode")
                : Component.translatable(SELECTED_SHADER_VALUE.displayName);
    }

    // ダークモード切替ボタンを作成
    public static Button createDarkModeButton(Screen screen) {
        var raw = com.sayuki.utils.config.ConfigManager.get().get("darkmode");
        if (!(raw instanceof DarkModeFeatureConfig cfg)) {
            return null;
        }

        if (!cfg.isEnabled()) return null;

        if (!(screen instanceof AbstractContainerScreen) && !(cfg.isShowButtonInTitleScreen() && screen instanceof TitleScreen)) {
            return null;
        }

        int x = cfg.getGuiButtonX();
        int y = cfg.getGuiButtonY();
        if (screen instanceof TitleScreen) {
            x = cfg.getTitleScreenButtonX();
            y = cfg.getTitleScreenButtonY();
        }

        Button button = Button.builder(Component.translatable("gui.sayukiutils.darkmode.dark_mode"), btn -> {
                    List<ShaderConfig.ShaderValue> shaders = CONFIG.getShaders();
                    if (shaders.isEmpty()) {
                        return;
                    }
                    int selectedShaderIndex = getNextShaderValueIndex();
                    if (selectedShaderIndex < 0 || selectedShaderIndex >= shaders.size()) {
                        return;
                    }
                    CONFIG.setSelectedShaderIndex(selectedShaderIndex);
                    SELECTED_SHADER_VALUE = shaders.get(selectedShaderIndex);
                    propagateDivideFactor();
                    btn.setMessage(getCurrentModeName());
                    btn.setTooltip(getShaderSwitchButtonTooltip());
                })
                .bounds(x, screen.height - 24 - y, 60, 20)
                .build();

        button.setTooltip(getShaderSwitchButtonTooltip());
        return button;
    }

    // 切替ボタンのツールチップを取得
    private static Tooltip getShaderSwitchButtonTooltip() {
        Component tooltip = getCurrentModeName().copy().append(Component.literal("\n"))
                .append(Component.translatable("gui.tooltip.sayukiutils.darkmode.shader_switch_tooltip").withStyle(ChatFormatting.GRAY));
        return Tooltip.create(tooltip);
    }

    @FunctionalInterface
    public interface ShaderRegistrar {
        // シェーダーを登録
        void registerShader(ResourceLocation location, VertexFormat format, Consumer<ShaderInstance> callback);
    }
}

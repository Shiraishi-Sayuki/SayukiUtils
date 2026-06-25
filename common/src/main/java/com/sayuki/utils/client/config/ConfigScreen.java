package com.sayuki.utils.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.sayuki.utils.config.ConfigManager;
import com.sayuki.utils.config.CopyUUIDFeatureConfig;
import com.sayuki.utils.config.FeatureConfig;
import com.sayuki.utils.darkmode.DarkModeFeatureConfig;
import com.sayuki.utils.lan.LanFeatureConfig;
import com.sayuki.utils.playtime.PlayTimeFeatureConfig;
import com.sayuki.utils.playtime.util.Color;
import com.sayuki.utils.playtime.util.ServerEntryRenderPos;
import com.sayuki.utils.playtime.util.WorldEntryRenderPos;

import java.util.ArrayList;

public class ConfigScreen {
    // 作成
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("sayukiutils.config.title"))
                .setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        buildDarkModeCategory(builder, eb);
        buildCopyUUIDCategory(builder, eb);
        buildEatanimCategory(builder, eb);
        buildPlayTimeCategory(builder, eb);
        buildLanCategory(builder, eb);

        return builder.build();
    }

    // ダークモード設定カテゴリーを構築
    private static void buildDarkModeCategory(ConfigBuilder builder, ConfigEntryBuilder eb) {
        DarkModeFeatureConfig cfg = ConfigManager.get().get("darkmode");
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("sayukiutils.config.feature.darkmode"));

        cat.addEntry(eb.startBooleanToggle(Component.translatable("sayukiutils.config.enabled"), cfg.isEnabled())
                .setDefaultValue(true)
                .setSaveConsumer(cfg::setEnabled)
                .build());

        SubCategoryBuilder general = eb.startSubCategory(Component.translatable("sayukiutils.config.darkmode.general"));
        general.add(eb.startBooleanToggle(Component.translatable("sayukiutils.config.darkmode.showButtonInTitleScreen"), cfg.isShowButtonInTitleScreen())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.showButtonInTitleScreen.tooltip"))
                .setSaveConsumer(cfg::setShowButtonInTitleScreen)
                .build());
        general.add(eb.startBooleanToggle(Component.translatable("sayukiutils.config.darkmode.methodShaderDump"), cfg.isMethodShaderDump())
                .setDefaultValue(false)
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.methodShaderDump.tooltip"))
                .setSaveConsumer(cfg::setMethodShaderDump)
                .build());
        cat.addEntry(general.build());

        SubCategoryBuilder btnPos = eb.startSubCategory(Component.translatable("sayukiutils.config.darkmode.buttonPosition"));
        btnPos.add(eb.startIntField(Component.translatable("sayukiutils.config.darkmode.guiButtonX"), cfg.getGuiButtonX())
                .setDefaultValue(32)
                .setMin(0)
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.guiButtonX.tooltip"))
                .setSaveConsumer(cfg::setGuiButtonX)
                .build());
        btnPos.add(eb.startIntField(Component.translatable("sayukiutils.config.darkmode.guiButtonY"), cfg.getGuiButtonY())
                .setDefaultValue(2)
                .setMin(0)
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.guiButtonY.tooltip"))
                .setSaveConsumer(cfg::setGuiButtonY)
                .build());
        btnPos.add(eb.startIntField(Component.translatable("sayukiutils.config.darkmode.titleScreenButtonX"), cfg.getTitleScreenButtonX())
                .setDefaultValue(4)
                .setMin(0)
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.titleScreenButtonX.tooltip"))
                .setSaveConsumer(cfg::setTitleScreenButtonX)
                .build());
        btnPos.add(eb.startIntField(Component.translatable("sayukiutils.config.darkmode.titleScreenButtonY"), cfg.getTitleScreenButtonY())
                .setDefaultValue(40)
                .setMin(0)
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.titleScreenButtonY.tooltip"))
                .setSaveConsumer(cfg::setTitleScreenButtonY)
                .build());
        cat.addEntry(btnPos.build());

        cat.addEntry(eb.startStrList(Component.translatable("sayukiutils.config.darkmode.methodShaderBlacklist"), cfg.getMethodShaderBlacklist())
                .setDefaultValue(new ArrayList<>(java.util.Arrays.asList(
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
                )))
                .setTooltip(Component.translatable("sayukiutils.config.darkmode.methodShaderBlacklist.tooltip"))
                .setSaveConsumer(cfg::setMethodShaderBlacklist)
                .build());
    }

    // 食べ物アニメーション設定カテゴリーを構築
    private static void buildEatanimCategory(ConfigBuilder builder, ConfigEntryBuilder eb) {
        FeatureConfig cfg = ConfigManager.get().get("eatanim");
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("sayukiutils.config.feature.eatanim"));

        cat.addEntry(eb.startBooleanToggle(Component.translatable("sayukiutils.config.enabled"), cfg.isEnabled())
                .setDefaultValue(true)
                .setSaveConsumer(cfg::setEnabled)
                .build());
    }

    // プレイ時間設定カテゴリーを構築
    private static void buildPlayTimeCategory(ConfigBuilder builder, ConfigEntryBuilder eb) {
        PlayTimeFeatureConfig cfg = ConfigManager.get().get("playtime");
        ConfigCategory worldCat = builder.getOrCreateCategory(Component.translatable("sayukiutils.config.feature.playtime"));

        worldCat.addEntry(eb.startBooleanToggle(Component.translatable("sayukiutils.config.enabled"), cfg.isEnabled())
                .setDefaultValue(true)
                .setSaveConsumer(cfg::setEnabled)
                .build());

        SubCategoryBuilder worldSc = eb.startSubCategory(Component.translatable("sayukiutils.config.playtime.worldEntry"));
        worldSc.add(eb.startBooleanToggle(Component.translatable("sayukiutils.config.playtime.showWorldPlayTime"), cfg.isShowWorldPlayTime())
                .setDefaultValue(true)
                .setSaveConsumer(cfg::setShowWorldPlayTime)
                .build());
        worldSc.add(eb.startEnumSelector(Component.translatable("sayukiutils.config.playtime.worldPlayTimePosition"), WorldEntryRenderPos.class, cfg.getWorldPlayTimePosition())
                .setDefaultValue(WorldEntryRenderPos.TOP_RIGHT)
                .setSaveConsumer(cfg::setWorldPlayTimePosition)
                .setEnumNameProvider(e -> Component.translatable("sayukiutils.config.playtime.worldPlayTimePosition." + e.name().toLowerCase()))
                .build());
        worldSc.add(eb.startAlphaColorField(Component.translatable("sayukiutils.config.playtime.worldPlayTimeColor"), cfg.getWorldPlayTimeColor().toARGB())
                .setDefaultValue(new Color(128, 128, 128, 255)::toARGB)
                .setSaveConsumer(cfg::setWorldPlayTimeColorFromARGB)
                .build());
        worldCat.addEntry(worldSc.build());

        SubCategoryBuilder serverSc = eb.startSubCategory(Component.translatable("sayukiutils.config.playtime.serverEntry"));
        serverSc.add(eb.startBooleanToggle(Component.translatable("sayukiutils.config.playtime.showServerPlayTime"), cfg.isShowServerPlayTime())
                .setDefaultValue(false)
                .setSaveConsumer(cfg::setShowServerPlayTime)
                .build());
        serverSc.add(eb.startEnumSelector(Component.translatable("sayukiutils.config.playtime.serverPlayTimePosition"), ServerEntryRenderPos.class, cfg.getServerPlayTimePosition())
                .setDefaultValue(ServerEntryRenderPos.AFTER_NAME)
                .setSaveConsumer(cfg::setServerPlayTimePosition)
                .setEnumNameProvider(e -> Component.translatable("sayukiutils.config.playtime.serverPlayTimePosition." + e.name().toLowerCase()))
                .build());
        serverSc.add(eb.startAlphaColorField(Component.translatable("sayukiutils.config.playtime.serverPlayTimeColor"), cfg.getServerPlayTimeColor().toARGB())
                .setDefaultValue(new Color(128, 128, 128, 255)::toARGB)
                .setSaveConsumer(cfg::setServerPlayTimeColorFromARGB)
                .build());
        worldCat.addEntry(serverSc.build());
    }

    // LAN設定カテゴリーを構築
    private static void buildLanCategory(ConfigBuilder builder, ConfigEntryBuilder eb) {
        LanFeatureConfig cfg = ConfigManager.get().get("lan");
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("sayukiutils.config.feature.lan"));

        cat.addEntry(eb.startBooleanToggle(Component.translatable("sayukiutils.config.enabled"), cfg.isEnabled())
                .setDefaultValue(true)
                .setSaveConsumer(cfg::setEnabled)
                .build());

        cat.addEntry(eb.startStrField(Component.translatable("sayukiutils.config.lan.ngrokAuthtoken"), cfg.getNgrokAuthtoken())
                .setDefaultValue("")
                .setTooltip(Component.translatable("sayukiutils.config.lan.ngrokAuthtoken.tooltip"))
                .setSaveConsumer(cfg::setNgrokAuthtoken)
                .build());


    }

    // CopyUUID設定カテゴリーを構築
    private static void buildCopyUUIDCategory(ConfigBuilder builder, ConfigEntryBuilder eb) {
        CopyUUIDFeatureConfig cfc = ConfigManager.get().get("copyuuid");
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("sayukiutils.config.feature.copyuuid"));

        cat.addEntry(eb.startBooleanToggle(Component.translatable("sayukiutils.config.enabled"), cfc.isEnabled())
                .setDefaultValue(true)
                .setSaveConsumer(cfc::setEnabled)
                .build());

        cat.addEntry(eb.startStringDropdownMenu(
                        Component.translatable("sayukiutils.config.copyuuid.triggerItem"),
                        cfc.getTriggerItem())
                .setSelections(BuiltInRegistries.ITEM.keySet().stream()
                        .map(ResourceLocation::toString)
                        .sorted()
                        .toList())
                .setDefaultValue("minecraft:stick")
                .setTooltip(Component.translatable("sayukiutils.config.copyuuid.triggerItem.tooltip"))
                .setSaveConsumer(cfc::setTriggerItem)
                .setSuggestionMode(true)
                .build());
    }
}

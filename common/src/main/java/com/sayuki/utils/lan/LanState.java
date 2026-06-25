package com.sayuki.utils.lan;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class LanState extends SavedData {
    public static final String DATA_NAME = "sayukiutils_lan";
    private static final String LAN_SETTINGS_KEY = "lanSettings";
    private static final String WHITELIST_ENABLED_KEY = "whitelistEnabled";

    private static final String GLOBAL_SETTINGS_KEY = "globalSettings";

    @Nullable
    private LanSettings lanSettings;
    @Nullable
    private LanSettings globalSettings;
    private boolean whitelistEnabled;

    private LanState(@Nullable LanSettings lanSettings, @Nullable LanSettings globalSettings, boolean whitelistEnabled) {
        this.lanSettings = lanSettings;
        this.globalSettings = globalSettings;
        this.whitelistEnabled = whitelistEnabled;
    }

    public LanState() {
        this.lanSettings = null;
        this.globalSettings = null;
        this.whitelistEnabled = false;
    }

    // NBTから読み込み
    public static LanState fromNbt(CompoundTag nbt) {
        CompoundTag lanSettingsNbt = nbt.getCompound(LAN_SETTINGS_KEY);
        CompoundTag globalSettingsNbt = nbt.getCompound(GLOBAL_SETTINGS_KEY);
        return new LanState(lanSettingsNbt.isEmpty() ? null : LanSettings.fromNbt(lanSettingsNbt),
                globalSettingsNbt.isEmpty() ? null : LanSettings.fromNbt(globalSettingsNbt),
                nbt.getBoolean(WHITELIST_ENABLED_KEY));
    }

    @Override
    // 保存
    public CompoundTag save(CompoundTag nbt) {
        if (lanSettings != null) {
            CompoundTag lanSettingsNbt = new CompoundTag();
            lanSettings.writeNbt(lanSettingsNbt);
            nbt.put(LAN_SETTINGS_KEY, lanSettingsNbt);
        }
        if (globalSettings != null) {
            CompoundTag globalSettingsNbt = new CompoundTag();
            globalSettings.writeNbt(globalSettingsNbt);
            nbt.put(GLOBAL_SETTINGS_KEY, globalSettingsNbt);
        }
        nbt.putBoolean(WHITELIST_ENABLED_KEY, this.whitelistEnabled);
        return nbt;
    }

    @Nullable
    // LanSettingsを取得
    public LanSettings getLanSettings() {
        return this.lanSettings;
    }

    @Nullable
    // GlobalSettingsを取得
    public LanSettings getGlobalSettings() {
        return this.globalSettings;
    }

    // WhitelistEnabledを取得
    public boolean getWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    // LanSettingsを設定
    public void setLanSettings(@Nullable LanSettings lanSettings) {
        this.lanSettings = lanSettings;
        this.setDirty();
    }

    // GlobalSettingsを設定
    public void setGlobalSettings(@Nullable LanSettings globalSettings) {
        this.globalSettings = globalSettings;
        this.setDirty();
    }

    // ホワイトリスト有効を設定
    public void setWhitelistEnabled(boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
        this.setDirty();
    }
}

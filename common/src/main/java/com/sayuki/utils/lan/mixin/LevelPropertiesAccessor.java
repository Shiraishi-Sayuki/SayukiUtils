package com.sayuki.utils.lan.mixin;

import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PrimaryLevelData.class)
public interface LevelPropertiesAccessor {
    @Accessor("settings")
    // settingsフィールドへのアクセサ
    LevelSettings getSettings();

    @Accessor("settings")
    // settingsフィールドへのアクセサ
    void setSettings(LevelSettings settings);
}

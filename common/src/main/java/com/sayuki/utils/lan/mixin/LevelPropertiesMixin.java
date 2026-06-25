package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.sayuki.utils.lan.SetCommandsAllowed;

import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;

@Mixin(PrimaryLevelData.class)
public class LevelPropertiesMixin implements SetCommandsAllowed {
    @Unique
    @Override
    // コマンド許可を設定
    public void setCommandsAllowed(boolean allowCommands) {
        LevelPropertiesAccessor self = (LevelPropertiesAccessor) this;
        LevelSettings settings = self.getSettings();
        self.setSettings(new LevelSettings(settings.levelName(), settings.gameType(), settings.hardcore(),
                settings.difficulty(), allowCommands, settings.gameRules(), settings.getDataConfiguration()));
    }
}

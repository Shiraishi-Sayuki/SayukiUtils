package com.sayuki.utils.playtime.mixin;

import com.sayuki.utils.playtime.util.IWithPlayTime;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelSummary.class)
public class LevelSummaryMixin implements IWithPlayTime {
    @Unique
    private int sayukiutils$playTimeTicks = -1;

    @Override
    // プレイ時間を設定
    public void setPlayTimeTicks(int playTimeTicks) {
        this.sayukiutils$playTimeTicks = playTimeTicks;
    }

    @Override
    // プレイ時間を取得
    public int getPlayTimeTicks() {
        return this.sayukiutils$playTimeTicks;
    }
}

package com.sayuki.utils.playtime.mixin;

import com.sayuki.utils.playtime.PlayTimeFeatureConfig;
import com.sayuki.utils.playtime.util.IWithPlayTime;
import com.sayuki.utils.playtime.util.PlayTimeRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class WorldListEntryMixin {

    @Inject(at = @At("TAIL"), method = "render")
    // 描画
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int width, int height, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick, CallbackInfo ci) {
        PlayTimeFeatureConfig cfg = com.sayuki.utils.config.ConfigManager.get().get("playtime");
        if (cfg == null || !cfg.isShowWorldPlayTime()) return;

        LevelSummary summary = ((WorldSelectionListAccessor) this).getSummary();
        if (summary instanceof IWithPlayTime withPlayTime) {
            int ticks = withPlayTime.getPlayTimeTicks();
            int indicatorWidth = PlayTimeRenderer.getWholeWidth(ticks);
            if (indicatorWidth == 0) return;

            int renderX, renderY;
            switch (cfg.getWorldPlayTimePosition()) {
                case TOP_RIGHT -> {
                    renderX = x + width - indicatorWidth - 4;
                    renderY = y;
                }
                case LEFT -> {
                    renderX = x - indicatorWidth - 5;
                    renderY = y + 10;
                }
                case RIGHT -> {
                    renderX = x + width + 14;
                    renderY = y + 10;
                }
                default -> { return; }
            }

            PlayTimeRenderer.render(guiGraphics, renderX, renderY, ticks, cfg.getWorldPlayTimeColor());
        }
    }
}

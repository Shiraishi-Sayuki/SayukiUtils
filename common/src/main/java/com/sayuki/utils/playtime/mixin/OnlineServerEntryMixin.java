package com.sayuki.utils.playtime.mixin;

import com.sayuki.utils.playtime.PlayTimeFeatureConfig;
import com.sayuki.utils.playtime.ServerPlayTimeManager;
import com.sayuki.utils.playtime.util.PlayTimeRenderer;
import com.sayuki.utils.playtime.util.ServerEntryRenderPos;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerSelectionList.OnlineServerEntry.class, priority = 2000)
public class OnlineServerEntryMixin {

    @Unique
    private static int sayukiutils$serverNameStartX;

    @ModifyArg(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 0),
            method = "render",
            index = 2
    )
    // サーバー名X座標を加工
    public int serverNameX(int x) {
        sayukiutils$serverNameStartX = x;
        return x;
    }

    @Inject(at = @At("TAIL"), method = "render")
    // 描画時処理
    public void onRender(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        PlayTimeFeatureConfig cfg = com.sayuki.utils.config.ConfigManager.get().get("playtime");
        if (cfg == null || !cfg.isShowServerPlayTime()) return;

        var accessor = (OnlineServerEntryAccessor) this;
        int playTime = ServerPlayTimeManager.getPlayTime(accessor.getServerData().ip);
        int playTimeWidth = PlayTimeRenderer.getWholeWidth(playTime);
        if (playTimeWidth <= 0) return;

        int renderX, renderY;
        switch (cfg.getServerPlayTimePosition()) {
            case AFTER_NAME -> {
                int serverNameWidth = accessor.getMinecraft().font.width(accessor.getServerData().name);
                renderX = sayukiutils$serverNameStartX + 3 + serverNameWidth;
                renderY = y + 1;
            }
            case BEHIND_COUNT -> {
                int statusWidth = accessor.getMinecraft().font.width(accessor.getServerData().status);
                renderX = x + entryWidth - 24 - statusWidth - playTimeWidth;
                renderY = y;
            }
            case LEFT -> {
                renderX = x - playTimeWidth - 5;
                renderY = y + 10;
            }
            case RIGHT -> {
                renderX = x + entryWidth + 6;
                renderY = y + 10;
            }
            default -> { return; }
        }

        PlayTimeRenderer.render(guiGraphics, renderX, renderY, playTime, cfg.getServerPlayTimeColor());
    }
}

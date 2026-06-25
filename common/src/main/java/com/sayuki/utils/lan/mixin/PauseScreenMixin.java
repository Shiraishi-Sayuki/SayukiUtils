package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    private static final Component EDIT_LAN_TEXT = Component.translatable("sayukiutils.lan.editLan");

    @Inject(method = "init", at = @At("TAIL"))
    // LANボタンを置き換え
    private void replaceOpenToLanButton(CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        var server = minecraft.getSingleplayerServer();
        if (server == null || !server.isPublished()) return;

        Screen screen = (Screen) (Object) this;
        for (var widget : screen.children()) {
            if (widget instanceof Button button && button.getMessage().equals(
                    Component.translatable("menu.shareToLan"))) {
                button.setMessage(EDIT_LAN_TEXT);
                ((ButtonAccessor) button).setOnPress(btn ->
                        minecraft.setScreen(new ShareToLanScreen(null)));
                break;
            }
        }
    }
}

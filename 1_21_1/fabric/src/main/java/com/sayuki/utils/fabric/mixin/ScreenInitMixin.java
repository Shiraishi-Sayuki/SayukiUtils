/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.fabric.mixin;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sayuki.utils.darkmode.SayukiDarkModeClient;

@Mixin(Screen.class)
public abstract class ScreenInitMixin {
    @Shadow
    private java.util.List<Renderable> renderables;

    @Shadow
    private java.util.List<GuiEventListener> children;

    @Shadow
    private java.util.List<NarratableEntry> narratables;

    @Inject(method = "init", at = @At("TAIL"))
    private void afterInit(CallbackInfo ci) {
        var btn = SayukiDarkModeClient.createDarkModeButton((Screen)(Object)this);
        if (btn != null) {
            this.renderables.add(btn);
            this.children.add(btn);
            this.narratables.add(btn);
        }
    }
}

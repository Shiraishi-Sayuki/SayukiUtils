/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.GridLayout;

@Mixin(GridLayout.class)
public interface GridWidgetAccessor {
    @Accessor("children")
    // childrenフィールドへのアクセサ
    List<Renderable> getChildren();
}

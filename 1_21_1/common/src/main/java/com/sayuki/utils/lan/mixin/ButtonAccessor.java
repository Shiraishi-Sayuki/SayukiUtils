/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;

@Mixin(Button.class)
public interface ButtonAccessor {
    @Accessor("onPress")
    @Mutable
    // onPressフィールドへのアクセサ
    void setOnPress(OnPress onPress);
}

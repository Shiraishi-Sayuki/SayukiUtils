/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.darkmode.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

@Mixin(ShaderInstance.class)
public interface ShaderInstanceAccessor {

    @Invoker("getUniform")
    // ユニフォームを取得（Invoker）
    Uniform invokeGetUniform(String name);
}

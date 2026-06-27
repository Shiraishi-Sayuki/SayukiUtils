/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.darkmode.mixins;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;

import com.sayuki.utils.darkmode.SayukiDarkModeClient;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

    @Inject(method = "_drawWithShader", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"))
    // DivideFactorユニフォームを設定
    private void darkModeEverywhere$_drawWithShader(Matrix4f p_253705_, Matrix4f p_253737_, ShaderInstance shaderInstance, CallbackInfo ci) {
        var selectedShaderValue = SayukiDarkModeClient.getSelectedShaderValue();
        if (selectedShaderValue == null) return;

        Uniform divideFactor = ((ShaderInstanceAccessor) shaderInstance).invokeGetUniform("DivideFactor");
        if (divideFactor != null) {
            divideFactor.set(selectedShaderValue.divideFactor);
        }
    }
}

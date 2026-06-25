package com.sayuki.utils.darkmode.mixins;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;

import com.sayuki.utils.darkmode.ClassUtil;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;

@Mixin(GameRenderer.class)
public class GameRenderMixin {

    @Unique
    // デフォルトシェーダーを置き換え
    private static void darkModeEverywhere$replaceDefaultShader(CallbackInfoReturnable<ShaderInstance> cir, Supplier<ShaderInstance> replacer) {
        ShaderInstance replacement = replacer.get();
        if (replacement == null) return;
        cir.setReturnValue(replacer.get());
    }

    @Unique
    // 適宜シェーダーを置き換え
    private static void darkModeEverywhere$replaceDefaultShaderWhenAppropriate(CallbackInfoReturnable<ShaderInstance> cir, Supplier<ShaderInstance> replacer) {
        if (!SayukiDarkModeClient.isActive()) return;

        var callerClassName = ClassUtil.getCallerClassName();
        if (callerClassName == null) {
            darkModeEverywhere$replaceDefaultShader(cir, replacer);
            return;
        }

        boolean elementNameIsBlacklisted = SayukiDarkModeClient.isElementNameBlacklisted(callerClassName);

        if (!elementNameIsBlacklisted) {
            darkModeEverywhere$replaceDefaultShader(cir, replacer);
        }
    }

    @Inject(method = "getPositionTexShader", at = @At("HEAD"), cancellable = true)
    // position_texシェーダーを差し替え
    private static void getPositionTexShader(CallbackInfoReturnable<ShaderInstance> cir) {
        darkModeEverywhere$replaceDefaultShaderWhenAppropriate(cir, SayukiDarkModeClient::getSelectedTexShader);
    }

    @Inject(method = "getPositionTexColorShader", at = @At("HEAD"), cancellable = true)
    // position_tex_colorシェーダーを差し替え
    private static void getPositionTexColorShader(CallbackInfoReturnable<ShaderInstance> cir) {
        darkModeEverywhere$replaceDefaultShaderWhenAppropriate(cir, SayukiDarkModeClient::getSelectedTexColorShader);
    }
}

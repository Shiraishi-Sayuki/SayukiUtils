package com.sayuki.utils.darkmode.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FastColor;

import com.sayuki.utils.darkmode.ClassUtil;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;
import com.sayuki.utils.darkmode.ShaderConfig;

@Mixin(Font.class)
public class FontMixin {

    @Inject(method = "adjustColor", at = @At(value = "HEAD"), cancellable = true)
    // 暗いフォント色を置き換え
    private static void darkModeEverywhere$adjustColor(int color, CallbackInfoReturnable<Integer> cir) {
        ShaderConfig.ShaderValue shaderValue = SayukiDarkModeClient.SELECTED_SHADER_VALUE;
        if (shaderValue == null || Minecraft.getInstance().screen == null) return;

        var callerClassName = ClassUtil.getCallerClassName();
        if (callerClassName != null && SayukiDarkModeClient.isElementNameBlacklisted(callerClassName)) {
            return;
        }

        int threshold = 65;
        if (shaderValue.darkColorReplacement == -1) return;
        Integer gray = ChatFormatting.GRAY.getColor();
        Integer darkGray = ChatFormatting.DARK_GRAY.getColor();
        if ((gray != null && gray.equals(color)) || (darkGray != null && darkGray.equals(color))) {
            cir.setReturnValue(0xFF000000 | shaderValue.darkColorReplacement);
            return;
        }
        if (FastColor.ARGB32.red(color) < threshold && FastColor.ARGB32.green(color) < threshold && FastColor.ARGB32.blue(color) < threshold) {
            cir.setReturnValue(0xFF000000 | shaderValue.darkColorReplacement);
        }
    }
}

package com.sayuki.utils.darkmode;

import java.util.stream.Stream;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

public class ClassUtil {

    // 呼び出し元のクラス名を特定
    private static String walkForCallerClassName(Stream<StackWalker.StackFrame> stackFrameStream) {
        return stackFrameStream
                .filter(frame -> !(
                        frame.getClassName().equals(GameRenderer.class.getName())
                                || frame.getClassName().equals(ClassUtil.class.getName())
                                || frame.getClassName().equals(RenderSystem.class.getName())
                                || frame.getClassName().startsWith("java.lang.Thread")
                                || frame.getClassName().equals(GuiGraphics.class.getName())
                ))
                .findFirst()
                .map(f -> f.getClassName() + ":" + f.getMethodName())
                .orElse(null);
    }

    // 呼び出し元クラス名を取得
    public static String getCallerClassName() {
        return StackWalker.getInstance()
                .walk(ClassUtil::walkForCallerClassName);
    }
}

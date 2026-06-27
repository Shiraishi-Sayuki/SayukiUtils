/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Locale;

public class PlayTimeRenderer {
    private static final ResourceLocation TIME_ICON = new ResourceLocation("sayukiutils", "textures/gui/time_icon.png");

    @Nullable
    // プレイ時間コンポーネントを取得
    public static Component getPlayTimeComponent(int ticks) {
        if (ticks <= 0) return null;
        double hours = (ticks / 20.0) / 3600.0;
        return Component.translatable("sayukiutils.playtime.format", Component.literal(
                hours >= 100.0 ? String.valueOf((int) hours) : String.format(Locale.US, "%.1f", hours)
        ));
    }

    // 全体の幅を取得
    public static int getWholeWidth(int ticks) {
        Component component = getPlayTimeComponent(ticks);
        if (component == null) return 0;
        return Minecraft.getInstance().font.width(component) + 11;
    }

    // 描画
    public static void render(GuiGraphics guiGraphics, int x, int y, int playTimeTicks, Color color) {
        Minecraft minecraft = Minecraft.getInstance();
        Component component = getPlayTimeComponent(playTimeTicks);
        if (component == null) return;

        guiGraphics.setColor(color.r / 255.f, color.g / 255.f, color.b / 255.f, color.a / 255.f);
        guiGraphics.blit(TIME_ICON, x, y, 0.f, 0.f, 9, 9, 9, 9);
        guiGraphics.setColor(1.f, 1.f, 1.f, 1.f);
        guiGraphics.drawString(minecraft.font, component, x + 11, y + 1, color.toARGB(), false);
    }
}

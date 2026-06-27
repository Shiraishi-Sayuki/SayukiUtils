/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

public final class Utils {
    private Utils() {
    }

    // リンクコンポーネントを作成
    public static Component createLink(String url) {
        return Component.literal(url).withStyle(style -> style
                .withColor(ChatFormatting.BLUE)
                .withUnderlined(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }
}

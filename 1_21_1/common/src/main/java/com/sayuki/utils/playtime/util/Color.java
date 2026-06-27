/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime.util;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {
    private static final Pattern SAVE_PATTERN = Pattern.compile("\\((\\d+), (\\d+), (\\d+), (\\d+)\\)");

    public int r, g, b, a;

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    // Stringに変換
    public String toString() {
        return String.format("(%d, %d, %d, %d)", r, g, b, a);
    }

    @Nullable
    // Stringから変換
    public static Color fromString(String string) {
        Matcher matcher = SAVE_PATTERN.matcher(string);
        if (matcher.matches()) {
            try {
                return new Color(
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4))
                );
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    // ARGBから変換
    public static Color fromARGB(int argb) {
        return new Color(
                (argb >> 16) & 0xFF,
                (argb >> 8) & 0xFF,
                argb & 0xFF,
                (argb >> 24) & 0xFF
        );
    }

    // ARGBに変換
    public int toARGB() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

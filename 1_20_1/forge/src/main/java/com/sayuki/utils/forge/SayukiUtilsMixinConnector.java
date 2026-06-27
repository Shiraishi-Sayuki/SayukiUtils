/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.forge;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class SayukiUtilsMixinConnector implements IMixinConnector {
    @Override
    // Mixin設定を追加
    public void connect() {
        Mixins.addConfiguration("sayukiutils.forge.mixins.json");
        Mixins.addConfiguration("sayukiutils.tooltip.mixins.json");
        Mixins.addConfiguration("sayukiutils.lan.mixins.json");
    }
}

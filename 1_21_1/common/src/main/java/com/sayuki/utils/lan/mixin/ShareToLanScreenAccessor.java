/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.world.level.GameType;

@Mixin(ShareToLanScreen.class)
public interface ShareToLanScreenAccessor {
    @Accessor("gameMode")
    // gameModeフィールドへのアクセサ
    GameType getGameMode();

    @Accessor("gameMode")
    // gameModeフィールドへのアクセサ
    void setGameMode(GameType gameMode);

    @Accessor("port")
    // portフィールドへのアクセサ
    int getPort();

    @Accessor("port")
    // portフィールドへのアクセサ
    void setPort(int port);

    @Accessor("portEdit")
    // portEditフィールドへのアクセサ
    EditBox getPortEdit();
}

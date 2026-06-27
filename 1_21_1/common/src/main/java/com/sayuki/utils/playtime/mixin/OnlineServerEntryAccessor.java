/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public interface OnlineServerEntryAccessor {
    @Accessor("serverData")
    // serverDataフィールドへのアクセサ
    ServerData getServerData();
    @Accessor("minecraft")
    // minecraftフィールドへのアクセサ
    Minecraft getMinecraft();
}

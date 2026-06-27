/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.server.LanServerPinger;

@Mixin(IntegratedServer.class)
public interface IntegratedServerAccessor {
    @Accessor
    // LanPingerフィールドへのアクセサ
    LanServerPinger getLanPinger();

    @Accessor
    // LanPingerフィールドへのアクセサ
    void setLanPinger(LanServerPinger lanPinger);

    @Accessor("publishedPort")
    // publishedPortフィールドへのアクセサ
    void setPublishedPort(int publishedPort);

    @Accessor("uuid")
    // uuidフィールドへのアクセサ
    UUID getLocalPlayerUuid();
}

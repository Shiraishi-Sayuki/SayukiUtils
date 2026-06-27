/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserWhiteList;

@Mixin(PlayerList.class)
public interface PlayerListAccessor {
    @Accessor("maxPlayers")
    @Mutable
    // maxPlayersフィールドへのアクセサ
    void setMaxPlayers(int maxPlayers);

    @Accessor("LOGGER")
    // LOGGERフィールドへのアクセサ
    Logger getLogger();

    @Accessor("server")
    // serverフィールドへのアクセサ
    MinecraftServer getServer();

    @Accessor("ops")
    @Mutable
    // opsフィールドへのアクセサ
    ServerOpList getOps();

    @Accessor("ops")
    @Mutable
    // opsフィールドへのアクセサ
    void setOps(ServerOpList ops);

    @Accessor("bans")
    @Mutable
    // bansフィールドへのアクセサ
    UserBanList getBans();

    @Accessor("bans")
    @Mutable
    // bansフィールドへのアクセサ
    void setBans(UserBanList bans);

    @Accessor("ipBans")
    @Mutable
    // ipBansフィールドへのアクセサ
    IpBanList getIpBans();

    @Accessor("ipBans")
    @Mutable
    // ipBansフィールドへのアクセサ
    void setIpBans(IpBanList ipBans);

    @Accessor("whitelist")
    @Mutable
    // whitelistフィールドへのアクセサ
    UserWhiteList getWhitelist();

    @Accessor("whitelist")
    @Mutable
    // whitelistフィールドへのアクセサ
    void setWhitelist(UserWhiteList whitelist);

    @Accessor("doWhiteList")
    // doWhiteListフィールドへのアクセサ
    boolean isDoWhiteList();

    @Accessor("doWhiteList")
    // doWhiteListフィールドへのアクセサ
    void setDoWhiteList(boolean doWhiteList);
}

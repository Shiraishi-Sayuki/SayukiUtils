/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

public class PlayTimeClient {
    private static int playTicks;

    // クライアントティック処理
    public static void onClientTick() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;

        ServerData serverData = connection.getServerData();
        if (serverData == null) return;

        ServerPlayTimeManager.onPlayTick(serverData.ip);

        if (++playTicks >= 6000) {
            ServerPlayTimeManager.saveAsync();
            playTicks = 0;
        }
    }

    // サーバー退出処理
    public static void onLeaveServer() {
        ServerPlayTimeManager.save();
    }
}

/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ServerPlayTimeManager {
    private static final File file = new File("data/servers_playtime.dat");
    private static final HashMap<String, Integer> serverPlayTimes = new HashMap<>();

    // 保存
    public static void save() {
        if (serverPlayTimes.isEmpty()) return;

        if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) return;

        CompoundTag compoundTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : serverPlayTimes.entrySet()) {
            compoundTag.putInt(entry.getKey(), entry.getValue());
        }

        try {
            NbtIo.write(compoundTag, file.toPath());
        } catch (IOException ignored) {}
    }

    // 読み込み
    public static void load() {
        if (!file.exists()) return;

        try {
            CompoundTag compoundTag = NbtIo.read(file.toPath());
            if (compoundTag == null) return;
            for (String serverIp : compoundTag.getAllKeys()) {
                int playTime = compoundTag.getInt(serverIp);
                if (playTime > 0) serverPlayTimes.put(serverIp, playTime);
            }
        } catch (IOException ignored) {}
    }

    // 非同期保存
    public static void saveAsync() {
        CompletableFuture.runAsync(ServerPlayTimeManager::save);
    }

    // プレイティック処理
    public static void onPlayTick(String serverIp) {
        serverPlayTimes.put(serverIp, serverPlayTimes.getOrDefault(serverIp, 0) + 1);
    }

    // プレイ時間を取得
    public static int getPlayTime(String serverIp) {
        return serverPlayTimes.getOrDefault(serverIp, 0);
    }
}

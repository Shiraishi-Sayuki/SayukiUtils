package com.sayuki.utils.lan;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;

public class LanSettings {
    public GameType gameType;
    public boolean onlineMode;
    public boolean pvpEnabled;
    public TunnelType tunnel;
    public int port;
    public int maxPlayers;
    public String motd;

    public LanSettings(GameType gameType, boolean onlineMode, boolean pvpEnabled, TunnelType tunnel, int port,
            int maxPlayers, String motd) {
        this.gameType = gameType;
        this.onlineMode = onlineMode;
        this.pvpEnabled = pvpEnabled;
        this.tunnel = tunnel;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.motd = motd;
    }

    // デフォルト設定を取得
    public static LanSettings systemDefaults(MinecraftServer server) {
        return new LanSettings(server.getDefaultGameType(), true, true, TunnelType.NONE, 25565, 8,
                "${username} - ${world}");
    }

    // NBTから読み込み
    public static LanSettings fromNbt(CompoundTag nbt) {
        return new LanSettings(GameType.byId(nbt.getInt("gameType")), nbt.getBoolean("onlineMode"),
                nbt.getBoolean("pvpEnabled"), TunnelType.byName(nbt.getString("tunnel")), nbt.getInt("port"),
                nbt.getInt("maxPlayers"), nbt.getString("motd"));
    }

    // NBTに書き込み
    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putInt("gameType", gameType.getId());
        nbt.putBoolean("onlineMode", onlineMode);
        nbt.putBoolean("pvpEnabled", pvpEnabled);
        nbt.putString("tunnel", tunnel.asString());
        nbt.putInt("port", port);
        nbt.putInt("maxPlayers", maxPlayers);
        nbt.putString("motd", motd);
        return nbt;
    }

    // JSONに変換
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("gameType", gameType.getId());
        json.addProperty("onlineMode", onlineMode);
        json.addProperty("pvpEnabled", pvpEnabled);
        json.addProperty("tunnel", tunnel.asString());
        json.addProperty("port", port);
        json.addProperty("maxPlayers", maxPlayers);
        json.addProperty("motd", motd);
        return json;
    }

    @Nullable
    // JSONから読み込み
    public static LanSettings fromJson(JsonObject json) {
        if (json == null || json.size() == 0) return null;
        return new LanSettings(GameType.byId(json.get("gameType").getAsInt()),
                json.get("onlineMode").getAsBoolean(),
                json.get("pvpEnabled").getAsBoolean(),
                TunnelType.byName(json.get("tunnel").getAsString()),
                json.get("port").getAsInt(),
                json.get("maxPlayers").getAsInt(),
                json.get("motd").getAsString());
    }
}

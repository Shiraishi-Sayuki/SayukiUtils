package com.sayuki.utils.lan;

import java.util.function.Function;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;

public class PublishCommandArgumentValues {
    public Function<CommandContext<CommandSourceStack>, Integer> getPort;
    public Function<CommandContext<CommandSourceStack>, Boolean> getOnlineMode;
    public Function<CommandContext<CommandSourceStack>, Boolean> getPvpEnabled;
    public Function<CommandContext<CommandSourceStack>, Integer> getMaxPlayers;
    public Function<CommandContext<CommandSourceStack>, GameType> getGameMode;
    public Function<CommandContext<CommandSourceStack>, TunnelType> getTunnel;
    public Function<CommandContext<CommandSourceStack>, String> getMotd;

    // 値を取得
    private static <T> T getValue(Function<CommandContext<CommandSourceStack>, LanSettings> getDefaultLanSettings,
            CommandContext<CommandSourceStack> context, Function<LanSettings, T> getSetting) {
        MinecraftServer server = context.getSource().getServer();
        LanSettings defaultLanSettings = getDefaultLanSettings.apply(context);
        return getSetting.apply(
                defaultLanSettings != null ? defaultLanSettings : LanSettings.systemDefaults(server));
    }

    public PublishCommandArgumentValues(
            Function<CommandContext<CommandSourceStack>, LanSettings> getDefaultLanSettings) {
        this.getPort = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.port);
        this.getOnlineMode = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.onlineMode);
        this.getPvpEnabled = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.pvpEnabled);
        this.getMaxPlayers = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.maxPlayers);
        this.getGameMode = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.gameType);
        this.getTunnel = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.tunnel);
        this.getMotd = context -> getValue(getDefaultLanSettings, context,
                defaultLanSettings -> defaultLanSettings.motd);
    }

    public PublishCommandArgumentValues(PublishCommandArgumentValues other) {
        this.getPort = other.getPort;
        this.getOnlineMode = other.getOnlineMode;
        this.getPvpEnabled = other.getPvpEnabled;
        this.getMaxPlayers = other.getMaxPlayers;
        this.getGameMode = other.getGameMode;
        this.getTunnel = other.getTunnel;
        this.getMotd = other.getMotd;
    }
}

/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.fabric;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import net.fabricmc.api.ModInitializer;

import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.WhitelistCommand;

import com.sayuki.utils.SayukiUtils;
import com.sayuki.utils.command.SayukiUtilsCommand;
import com.sayuki.utils.lan.cmd.argument.GameModeArgumentType;
import com.sayuki.utils.lan.cmd.argument.TunnelArgumentType;

public class SayukiUtilsFabric implements ModInitializer {
    @Override
    // 初期化
    public void onInitialize() {
        SayukiUtils.init(FabricLoader.getInstance().getConfigDir());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                SayukiUtilsCommand.register(dispatcher));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            OpCommand.register(dispatcher);
            DeOpCommands.register(dispatcher);
            BanPlayerCommands.register(dispatcher);
            BanIpCommands.register(dispatcher);
            BanListCommands.register(dispatcher);
            PardonCommand.register(dispatcher);
            PardonIpCommand.register(dispatcher);
            WhitelistCommand.register(dispatcher);
        });

        ArgumentTypeRegistry.registerArgumentType(
                new ResourceLocation("sayukiutils", "game_mode"),
                GameModeArgumentType.class,
                SingletonArgumentInfo.contextFree(GameModeArgumentType::gameMode));

        ArgumentTypeRegistry.registerArgumentType(
                new ResourceLocation("sayukiutils", "tunnel"),
                TunnelArgumentType.class,
                SingletonArgumentInfo.contextFree(TunnelArgumentType::tunnel));
    }
}

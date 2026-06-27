/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.neoforge;

import java.util.Map;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.WhitelistCommand;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import com.sayuki.utils.SayukiUtils;
import com.sayuki.utils.command.SayukiUtilsCommand;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;
import com.sayuki.utils.lan.cmd.argument.GameModeArgumentType;
import com.sayuki.utils.lan.cmd.argument.TunnelArgumentType;
import com.sayuki.utils.neoforge.event.NeoForgeConfigScreenEvent;

@Mod("sayukiutils")
public class SayukiUtilsNeoForge {
    public SayukiUtilsNeoForge() {
        SayukiUtils.init(FMLPaths.CONFIGDIR.get());
        SayukiDarkModeClient.init(FMLPaths.CONFIGDIR.get().resolve("sayukiutils"));

        NeoForgeConfigScreenEvent.init();

        var modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        modBus.addListener(this::onRegister);

        NeoForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void onRegister(RegisterEvent event) {
        event.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key(), helper -> {
            registerArgumentType(helper, "sayukiutils:game_mode", GameModeArgumentType.class,
                    SingletonArgumentInfo.contextFree(GameModeArgumentType::gameMode));
            registerArgumentType(helper, "sayukiutils:tunnel", TunnelArgumentType.class,
                    SingletonArgumentInfo.contextFree(TunnelArgumentType::tunnel));
        });
    }

    @SuppressWarnings("unchecked")
    private static <A extends com.mojang.brigadier.arguments.ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
            void registerArgumentType(RegisterEvent.RegisterHelper<ArgumentTypeInfo<?, ?>> helper, String id, Class<A> clazz,
                    ArgumentTypeInfo<A, T> info) {
        helper.register(ResourceLocation.parse(id), info);
        try {
            var field = ArgumentTypeInfos.class.getDeclaredField("BY_CLASS");
            field.setAccessible(true);
            ((Map<Class<?>, ArgumentTypeInfo<?, ?>>) field.get(null)).put(clazz, info);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SayukiUtilsCommand.register(event.getDispatcher());

        if (event.getCommandSelection() == net.minecraft.commands.Commands.CommandSelection.INTEGRATED) {
            OpCommand.register(event.getDispatcher());
            DeOpCommands.register(event.getDispatcher());
            BanPlayerCommands.register(event.getDispatcher());
            BanIpCommands.register(event.getDispatcher());
            BanListCommands.register(event.getDispatcher());
            PardonCommand.register(event.getDispatcher());
            PardonIpCommand.register(event.getDispatcher());
            WhitelistCommand.register(event.getDispatcher());
        }
    }
}

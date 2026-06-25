package com.sayuki.utils.forge;

import java.util.Map;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegisterEvent;

import com.sayuki.utils.SayukiUtils;
import com.sayuki.utils.command.SayukiUtilsCommand;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;
import com.sayuki.utils.forge.event.ForgeConfigScreenEvent;
import com.sayuki.utils.lan.cmd.argument.GameModeArgumentType;
import com.sayuki.utils.lan.cmd.argument.TunnelArgumentType;

@Mod("sayukiutils")
public class SayukiUtilsForge {
    public SayukiUtilsForge() {
        SayukiUtils.init(FMLPaths.CONFIGDIR.get());
        SayukiDarkModeClient.init(FMLPaths.CONFIGDIR.get().resolve("sayukiutils"));

        ForgeConfigScreenEvent.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SubscribeEvent
    // 登録イベント処理
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
            // 引数タイプを登録
            void registerArgumentType(RegisterEvent.RegisterHelper<ArgumentTypeInfo<?, ?>> helper, String id, Class<A> clazz,
                    ArgumentTypeInfo<A, T> info) {
        helper.register(new ResourceLocation(id), info);
        try {
            var field = ArgumentTypeInfos.class.getDeclaredField("BY_CLASS");
            field.setAccessible(true);
            ((Map<Class<?>, ArgumentTypeInfo<?, ?>>) field.get(null)).put(clazz, info);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    // コマンド登録イベント処理
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

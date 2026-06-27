/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import static com.sayuki.utils.lan.cmd.argument.GameModeArgumentType.gameMode;
import static com.sayuki.utils.lan.cmd.argument.GameModeArgumentType.getGameMode;
import static com.sayuki.utils.lan.cmd.argument.TunnelArgumentType.getTunnel;
import static com.sayuki.utils.lan.cmd.argument.TunnelArgumentType.tunnel;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

import com.sayuki.utils.lan.LanServer;
import com.sayuki.utils.lan.LanServerValues;
import com.sayuki.utils.lan.LanSettings;
import com.sayuki.utils.lan.LanState;
import com.sayuki.utils.lan.TunnelType;
import com.sayuki.utils.lan.PublishCommandArgumentValues;

@Mixin(PublishCommand.class)
public class PublishCommandMixin {
    @Unique
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.publish.failed"));

    private static final SimpleCommandExceptionType NOT_STARTED_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.publish.failed.not_started"));
    private static final SimpleCommandExceptionType STOP_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.publish.failed.stop"));
    private static final Component PUBLISH_STOPPED_TEXT = Component.translatable("commands.publish.stopped");

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    // 登録
    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CallbackInfo ci) {
        List<Pair<ArgumentBuilder<CommandSourceStack, ?>, Consumer<PublishCommandArgumentValues>>> arguments = Arrays
                .asList(Pair.of(argument("port", integer(-1, 65535)),
                        argumentValues -> argumentValues.getPort = context -> getInteger(context, "port")),
                        Pair.of(argument("onlineMode", bool()),
                                argumentValues -> argumentValues.getOnlineMode = context -> getBool(context, "onlineMode")),
                        Pair.of(argument("pvpEnabled", bool()),
                                argumentValues -> argumentValues.getPvpEnabled = context -> getBool(context, "pvpEnabled")),
                        Pair.of(argument("maxPlayers", integer(1, Integer.MAX_VALUE)),
                                argumentValues -> argumentValues.getMaxPlayers = context -> getInteger(context, "maxPlayers")),
                        Pair.of(argument("defaultGameMode", gameMode()),
                                argumentValues -> argumentValues.getGameMode = context -> getGameMode(context, "defaultGameMode")),
                        Pair.of(argument("tunnel", tunnel()),
                                argumentValues -> argumentValues.getTunnel = context -> getTunnel(context, "tunnel")),
                        Pair.of(argument("motd", greedyString()),
                                argumentValues -> argumentValues.getMotd = context -> getString(context, "motd")));

        Function<PublishCommandArgumentValues, Command<CommandSourceStack>> executeCommand = argumentValues -> context -> execute(
                context.getSource(), argumentValues.getOnlineMode.apply(context),
                argumentValues.getPvpEnabled.apply(context), argumentValues.getTunnel.apply(context),
                argumentValues.getPort.apply(context), argumentValues.getMaxPlayers.apply(context),
                argumentValues.getGameMode.apply(context), argumentValues.getMotd.apply(context));

        LiteralArgumentBuilder<CommandSourceStack> command = processThisAndArguments(literal("publish")
                .requires(source -> source.hasPermission(4)),
                new PublishCommandArgumentValues(context -> {
                    MinecraftServer server = context.getSource().getServer();
                    if (server.isPublished()) {
                        LanServerValues serverValues = (LanServerValues) server;
                        return new LanSettings(server.getDefaultGameType(),
                                server.usesAuthentication(), server.isPvpAllowed(), serverValues.getTunnelType(),
                                server.getPort(), server.getMaxPlayers(), serverValues.getRawMotd());
                    }
                    LanState lanState = server.overworld().getDataStorage()
                            .computeIfAbsent(LanState.factory(), LanState.DATA_NAME);
                    if (lanState.getLanSettings() != null) {
                        return lanState.getLanSettings();
                    }
                    return null;
                }), executeCommand, arguments.iterator())
                .then(processThisAndArguments(literal("perworld"),
                        new PublishCommandArgumentValues(
                                context -> context.getSource().getServer().overworld()
                                        .getDataStorage().computeIfAbsent(LanState.factory(),
                                                LanState.DATA_NAME).getLanSettings()),
                        executeCommand, arguments.iterator()))
                .then(processThisAndArguments(literal("system"),
                        new PublishCommandArgumentValues(
                                context -> LanSettings.systemDefaults(context.getSource().getServer())),
                        executeCommand, arguments.iterator()))
                .then(literal("stop").executes(context -> stop(context.getSource())));

        dispatcher.register(command);
        ci.cancel();
    }

    // 引数を処理
    private static ArgumentBuilder<CommandSourceStack, ?> processArguments(
            PublishCommandArgumentValues argumentValues,
            Function<PublishCommandArgumentValues, Command<CommandSourceStack>> executeCommand,
            Iterator<Pair<ArgumentBuilder<CommandSourceStack, ?>, Consumer<PublishCommandArgumentValues>>> arguments) {
        Pair<ArgumentBuilder<CommandSourceStack, ?>, Consumer<PublishCommandArgumentValues>> argument = arguments.next();
        argument.getRight().accept(argumentValues);
        ArgumentBuilder<CommandSourceStack, ?> newArgument = argument.getLeft()
                .executes(executeCommand.apply(new PublishCommandArgumentValues(argumentValues)));
        if (arguments.hasNext()) {
            newArgument = newArgument.then(processArguments(argumentValues, executeCommand, arguments));
        }
        return newArgument;
    }

    // 引数付きコマンドを処理
    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T processThisAndArguments(
            T builder, PublishCommandArgumentValues argumentValues,
            Function<PublishCommandArgumentValues, Command<CommandSourceStack>> executeCommand,
            Iterator<Pair<ArgumentBuilder<CommandSourceStack, ?>, Consumer<PublishCommandArgumentValues>>> arguments) {
        return builder.executes(executeCommand.apply(argumentValues))
                .then(processArguments(new PublishCommandArgumentValues(argumentValues), executeCommand, arguments));
    }

    // 実行
    private static int execute(CommandSourceStack source, boolean onlineMode, boolean pvpEnabled,
            TunnelType tunnel, int rawPort, int maxPlayers, GameType gameType, String rawMotd)
            throws CommandSyntaxException {
        int port = rawPort != -1 ? rawPort : HttpUtil.getAvailablePort();
        try {
            LanServer.startOrSaveLan(source.getServer(), gameType, onlineMode, pvpEnabled, tunnel,
                    port, maxPlayers, rawMotd,
                    text -> source.sendSuccess(() -> text, true),
                    () -> { throw new RuntimeException(ERROR_FAILED.create()); },
                    text -> source.sendFailure(text));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof CommandSyntaxException cause) {
                throw cause;
            }
            throw e;
        }
        return port;
    }

    // 停止
    private static int stop(CommandSourceStack source) throws CommandSyntaxException {
        MinecraftServer server = source.getServer();
        if (!server.isPublished()) {
            throw NOT_STARTED_EXCEPTION.create();
        }
        LanServer.stopLan(server,
                () -> source.sendSuccess(() -> PUBLISH_STOPPED_TEXT, true),
                () -> { throw new RuntimeException(STOP_FAILED_EXCEPTION.create()); },
                text -> source.sendFailure(text));
        return Command.SINGLE_SUCCESS;
    }
}

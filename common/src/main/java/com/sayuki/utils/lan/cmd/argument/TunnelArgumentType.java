package com.sayuki.utils.lan.cmd.argument;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import com.sayuki.utils.lan.TunnelType;

public class TunnelArgumentType implements ArgumentType<TunnelType> {
    public static final DynamicCommandExceptionType INVALID_TUNNEL_EXCEPTION = new DynamicCommandExceptionType(
            tunnel -> Component.translatable("argument.tunnel.invalid", tunnel));

    private TunnelArgumentType() {
    }

    // Tunnel引数タイプを作成
    public static TunnelArgumentType tunnel() {
        return new TunnelArgumentType();
    }

    // Tunnelを取得
    public static TunnelType getTunnel(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, TunnelType.class);
    }

    @Override
    // 解析
    public TunnelType parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        TunnelType tunnelType = TunnelType.byName(string, null);
        if (tunnelType == null) {
            throw INVALID_TUNNEL_EXCEPTION.create(string);
        }
        return tunnelType;
    }

    @Override
    // 候補を提示
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(TunnelType.values()).map(TunnelType::getName), builder);
    }
}

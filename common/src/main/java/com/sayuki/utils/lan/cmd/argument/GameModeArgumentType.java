package com.sayuki.utils.lan.cmd.argument;

import java.util.Arrays;
import java.util.Collection;
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
import net.minecraft.world.level.GameType;

public class GameModeArgumentType implements ArgumentType<GameType> {
    private static final Collection<String> EXAMPLES = Arrays.asList("survival", "creative");
    public static final DynamicCommandExceptionType INVALID_GAMEMODE_EXCEPTION = new DynamicCommandExceptionType(
            gameMode -> Component.translatable("argument.gameMode.invalid", gameMode));

    private GameModeArgumentType() {
    }

    // GameMode引数タイプを作成
    public static GameModeArgumentType gameMode() {
        return new GameModeArgumentType();
    }

    // GameModeを取得
    public static GameType getGameMode(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, GameType.class);
    }

    @Override
    // 解析
    public GameType parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        GameType gameType = GameType.byName(string, null);
        if (gameType == null) {
            throw INVALID_GAMEMODE_EXCEPTION.create(string);
        }
        return gameType;
    }

    @Override
    // 候補を提示
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(GameType.values()).map(GameType::getName), builder);
    }

    @Override
    // 使用例を取得
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

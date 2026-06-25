package com.sayuki.utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import com.sayuki.utils.config.ConfigManager;
import com.sayuki.utils.config.CopyUUIDFeatureConfig;
import com.sayuki.utils.config.FeatureConfig;

public class SayukiUtilsCommand {
    private static final SuggestionProvider<CommandSourceStack> ITEMS_SUGGESTIONS = (ctx, builder) -> {
        String prefix = builder.getRemaining().toLowerCase();
        BuiltInRegistries.ITEM.keySet().stream()
                .map(ResourceLocation::toString)
                .filter(id -> id.contains(prefix))
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    // 登録
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sayukiutils")
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            sendHelp(ctx, null);
                            return 1;
                        }))
                .then(Commands.literal("copyuuid")
                        .then(Commands.literal("help")
                                .executes(ctx -> {
                                    sendHelp(ctx, "copyuuid");
                                    return 1;
                                }))
                        .then(Commands.literal("toggle")
                                .executes(ctx -> toggleFeature(ctx, "copyuuid")))
                        .then(Commands.literal("reset")
                                .executes(SayukiUtilsCommand::resetTriggerItem))
                        .then(Commands.literal("set")
                                .executes(SayukiUtilsCommand::setTriggerItemFromHand)
                                .then(Commands.argument("item", StringArgumentType.greedyString())
                                        .suggests(ITEMS_SUGGESTIONS)
                                        .executes(SayukiUtilsCommand::setTriggerItem))))
                );
    }

    // ヘルプを送信
    private static void sendHelp(CommandContext<CommandSourceStack> ctx, String feature) {
        if (feature == null) {
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.title"), false);
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.line"), false);
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.sub"), false);
        } else if ("copyuuid".equals(feature)) {
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.title"), false);
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.desc"), false);
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.toggle"), false);
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.set"), false);
            ctx.getSource().sendSuccess(() -> Component.translatable("sayukiutils.help.copyuuid.reset"), false);
        }
    }

    // フィーチャーを切り替え
    private static int toggleFeature(CommandContext<CommandSourceStack> ctx, String featureId) {
        FeatureConfig cfg = ConfigManager.get().get(featureId);
        cfg.setEnabled(!cfg.isEnabled());
        ConfigManager.save();
        String featureName = Component.translatable("sayukiutils.config.feature." + featureId).getString();
        String status = Component.translatable(cfg.isEnabled() ? "sayukiutils.command.enabled" : "sayukiutils.command.disabled").getString();
        ctx.getSource().sendSuccess(() ->
                Component.translatable("sayukiutils.command.toggle", featureName, status), true);
        return 1;
    }

    // トリガーアイテムを設定
    private static int setTriggerItem(CommandContext<CommandSourceStack> ctx) {
        String raw = StringArgumentType.getString(ctx, "item");
        String itemId = raw.contains(":") ? raw : "minecraft:" + raw;
        if (!BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemId))) {
            ctx.getSource().sendFailure(Component.translatable("sayukiutils.command.unknownItem", itemId));
            return 0;
        }
        CopyUUIDFeatureConfig cfg = ConfigManager.get().get("copyuuid");
        cfg.setTriggerItem(itemId);
        ConfigManager.save();
        ctx.getSource().sendSuccess(() ->
                Component.translatable("sayukiutils.command.copyuuid.set", itemId), true);
        return 1;
    }

    // 手持ちのアイテムをトリガーに設定
    private static int setTriggerItemFromHand(CommandContext<CommandSourceStack> ctx) {
        var player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendFailure(Component.translatable("sayukiutils.command.playerOnly"));
            return 0;
        }
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) {
            ctx.getSource().sendFailure(Component.translatable("sayukiutils.command.copyuuid.set.heldAir"));
            return 0;
        }
        String itemId = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
        CopyUUIDFeatureConfig cfg = ConfigManager.get().get("copyuuid");
        cfg.setTriggerItem(itemId);
        ConfigManager.save();
        ctx.getSource().sendSuccess(() ->
                Component.translatable("sayukiutils.command.copyuuid.set", itemId), true);
        return 1;
    }

    // トリガーアイテムをリセット
    private static int resetTriggerItem(CommandContext<CommandSourceStack> ctx) {
        CopyUUIDFeatureConfig cfg = ConfigManager.get().get("copyuuid");
        cfg.setTriggerItem("minecraft:stick");
        ConfigManager.save();
        ctx.getSource().sendSuccess(() ->
                Component.translatable("sayukiutils.command.copyuuid.reset"), true);
        return 1;
    }
}

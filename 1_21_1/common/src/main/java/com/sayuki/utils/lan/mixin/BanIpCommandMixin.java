/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanList;

@Mixin(BanIpCommands.class)
public class BanIpCommandMixin {
    private static final SimpleCommandExceptionType CANNOT_BAN_HOST_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.banip.failed.host"));

    @Inject(method = "banIp", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/players/PlayerList;getPlayersWithAddress(Ljava/lang/String;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    // ホストをチェック
    private static void checkHost(CommandSourceStack source, String targetIp, @Nullable Component reason,
            CallbackInfoReturnable<Integer> ci, IpBanList bannedIpList, List<ServerPlayer> list)
            throws CommandSyntaxException {
        if (list.stream().anyMatch(target -> source.getServer().isSingleplayerOwner(target.getGameProfile()))) {
            throw CANNOT_BAN_HOST_EXCEPTION.create();
        }
    }
}

/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.BanPlayerCommands;

@Mixin(BanPlayerCommands.class)
public class BanPlayerCommandMixin {
    private static final SimpleCommandExceptionType CANNOT_BAN_HOST_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.ban.failed.host"));

    @ModifyVariable(method = "banPlayers", at = @At("HEAD"), argsOnly = true)
    // ホストをチェック
    private static Collection<GameProfile> checkHost(Collection<GameProfile> targets, CommandSourceStack source)
            throws CommandSyntaxException {
        List<GameProfile> newTargets = targets.stream().filter(target -> !source.getServer().isSingleplayerOwner(target))
                .collect(Collectors.toList());
        if (newTargets.isEmpty()) {
            throw CANNOT_BAN_HOST_EXCEPTION.create();
        }
        return newTargets;
    }
}

package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.level.ServerPlayer;

@Mixin({ OpCommand.class, DeOpCommands.class, BanPlayerCommands.class, BanIpCommands.class,
        BanListCommands.class, PardonCommand.class, PardonIpCommand.class, WhitelistCommand.class })
public class GenericCommandMixin {

    @Inject(method = "desc=/^\\(L(?:net\\/minecraft\\/commands\\/CommandSourceStack|net\\/minecraft\\/class_2168);\\)Z$/", at = @At("HEAD"), cancellable = true)
    // 権限をチェック
    private static void checkPermissions(CommandSourceStack source, CallbackInfoReturnable<Boolean> ci) {
        var entity = source.getEntity();
        if (entity instanceof ServerPlayer
                && source.getServer().isSingleplayerOwner(((ServerPlayer) entity).getGameProfile())) {
            ci.setReturnValue(true);
        }
    }
}

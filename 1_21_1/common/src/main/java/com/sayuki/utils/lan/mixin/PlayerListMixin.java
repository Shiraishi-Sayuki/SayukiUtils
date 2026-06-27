/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.world.level.storage.PlayerDataStorage;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    // ワールド固有ファイルに変換
    private File toWorldSpecificFile(File file) {
        return ((PlayerListAccessor) this).getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve(file.getPath()).toFile();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    // 初期化
    private void init(MinecraftServer server, LayeredRegistryAccess<RegistryLayer> registries,
            PlayerDataStorage playerIo, int maxPlayers, CallbackInfo ci) {
        PlayerListAccessor self = (PlayerListAccessor) this;
        self.setOps(new ServerOpList(this.toWorldSpecificFile(PlayerList.OPLIST_FILE)));
        self.setBans(new UserBanList(this.toWorldSpecificFile(PlayerList.USERBANLIST_FILE)));
        self.setIpBans(new IpBanList(this.toWorldSpecificFile(PlayerList.IPBANLIST_FILE)));
        self.setWhitelist(new UserWhiteList(this.toWorldSpecificFile(PlayerList.WHITELIST_FILE)));

        try {
            self.getOps().load();
        } catch (Exception e) {
            self.getLogger().warn("Failed to load operators list: ", e);
        }
        try {
            self.getBans().load();
        } catch (IOException e) {
            self.getLogger().warn("Failed to load user banlist: ", e);
        }
        try {
            self.getIpBans().load();
        } catch (IOException e) {
            self.getLogger().warn("Failed to load ip banlist: ", e);
        }
        try {
            self.getWhitelist().load();
        } catch (Exception e) {
            self.getLogger().warn("Failed to load white-list: ", e);
        }
    }

    @Inject(method = "canPlayerLogin", at = @At("HEAD"), cancellable = true)
    // 参加可能かチェック
    private void checkCanJoin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Component> ci) {
        if (((PlayerListAccessor) this).getServer().isSingleplayerOwner(profile)) {
            ci.setReturnValue(null);
        }
    }

    @Redirect(method = "op", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/ServerOpList;add(Lnet/minecraft/server/players/StoredUserEntry;)V"))
    // オペレーターに追加
    private void addToOperators(ServerOpList ops, StoredUserEntry<GameProfile> entry, GameProfile profile) {
        PlayerListAccessor self = (PlayerListAccessor) this;
        if (self.getServer().isSingleplayerOwner(profile)) {
            ((com.sayuki.utils.lan.SetCommandsAllowed) self.getServer().getWorldData()).setCommandsAllowed(true);
        } else {
            ops.add((ServerOpListEntry) entry);
            this.saveOpList();
        }
    }

    @Redirect(method = "deop", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/ServerOpList;remove(Ljava/lang/Object;)V"))
    // オペレーターから削除
    private void removeFromOperators(ServerOpList ops, Object entry, GameProfile profile) {
        PlayerListAccessor self = (PlayerListAccessor) this;
        if (self.getServer().isSingleplayerOwner(profile)) {
            ((com.sayuki.utils.lan.SetCommandsAllowed) self.getServer().getWorldData()).setCommandsAllowed(false);
        } else {
            ops.remove(profile);
            this.saveOpList();
        }
    }

    @Inject(method = "isOp", at = @At("HEAD"), cancellable = true)
    // オペレーターかどうか確認
    private void isOperator(GameProfile profile, CallbackInfoReturnable<Boolean> ci) {
        PlayerListAccessor self = (PlayerListAccessor) this;
        if (self.getServer().isSingleplayerOwner(profile)) {
            ci.setReturnValue(((com.sayuki.utils.lan.mixin.LevelPropertiesAccessor) self.getServer().getWorldData()).getSettings().allowCommands());
        } else {
            ci.setReturnValue(((StoredUserListAccessor<GameProfile>) self.getOps()).callContains(profile));
        }
    }

    @Inject(method = "getOpNames", at = @At("HEAD"), cancellable = true)
    // ホストをOP名前に追加
    private void addHostToOpNames(CallbackInfoReturnable<String[]> ci) {
        PlayerListAccessor self = (PlayerListAccessor) this;
        if (((com.sayuki.utils.lan.mixin.LevelPropertiesAccessor) self.getServer().getWorldData()).getSettings().allowCommands()) {
            ci.setReturnValue(ArrayUtils.insert(0, self.getOps().getUserList(), self.getServer().getSingleplayerProfile().getName()));
        }
    }

    @Inject(method = "canBypassPlayerLimit", at = @At("HEAD"), cancellable = true)
    // プレイヤー制限を回避できるか確認
    private void canBypassPlayerLimit(GameProfile profile, CallbackInfoReturnable<Boolean> ci) {
        if (((PlayerListAccessor) this).getOps().canBypassPlayerLimit(profile)) {
            ci.setReturnValue(true);
        }
    }

    // OPリストを保存
    private void saveOpList() {
        try {
            ((PlayerListAccessor) this).getOps().save();
        } catch (Exception e) {
            ((PlayerListAccessor) this).getLogger().warn("Failed to save operators list: ", e);
        }
    }

    // LAN状態を取得
    private com.sayuki.utils.lan.LanState getLanState() {
        return ((PlayerListAccessor) this).getServer().overworld().getDataStorage().computeIfAbsent(
                com.sayuki.utils.lan.LanState.factory(),
                com.sayuki.utils.lan.LanState.DATA_NAME);
    }

    @Inject(method = "isUsingWhitelist", at = @At("HEAD"), cancellable = true)
    // ホワイトリストが有効か確認
    private void isWhitelistEnabled(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(this.getLanState().getWhitelistEnabled());
    }

    @Inject(method = "setUsingWhiteList", at = @At("TAIL"))
    // ホワイトリスト有効を設定
    private void setWhitelistEnabled(boolean whitelistEnabled, CallbackInfo ci) {
        this.getLanState().setWhitelistEnabled(whitelistEnabled);
    }

    @Inject(method = "isWhiteListed", at = @At("HEAD"))
    // ホワイトリスト設定を更新
    private void updateWhitelistEnabled(GameProfile profile, CallbackInfoReturnable<Boolean> ci) {
        ((PlayerListAccessor) this).setDoWhiteList(((PlayerList) (Object) this).isUsingWhitelist());
    }
}

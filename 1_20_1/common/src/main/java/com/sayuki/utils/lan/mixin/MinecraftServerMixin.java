/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sayuki.utils.lan.LanServerValues;
import com.sayuki.utils.lan.TunnelType;
import com.sayuki.utils.lan.TunnelType.TunnelException;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements LanServerValues {
    private TunnelType tunnelType = TunnelType.NONE;
    private Component tunnelText = null;
    private String rawMotd = null;

    @Inject(at = @At("TAIL"), method = "stopServer")
    // シャットダウン後処理
    private void postShutdown(CallbackInfo ci) {
        try {
            this.getTunnelType().stop((MinecraftServer) (Object) this);
        } catch (TunnelException e) {
            e.printStackTrace();
        }
    }

    @Override
    // TunnelTypeを取得
    public TunnelType getTunnelType() {
        return this.tunnelType;
    }

    @Override
    // TunnelTypeを設定
    public void setTunnelType(TunnelType tunnelType) {
        this.tunnelType = tunnelType;
    }

    @Override
    // TunnelTextを取得
    public Component getTunnelText() {
        return this.tunnelText;
    }

    @Override
    // TunnelTextを設定
    public void setTunnelText(Component tunnelText) {
        this.tunnelText = tunnelText;
    }

    @Override
    // RawMotdを取得
    public String getRawMotd() {
        return this.rawMotd;
    }

    @Override
    // RawMotdを設定
    public void setRawMotd(String rawMotd) {
        this.rawMotd = rawMotd;
    }
}

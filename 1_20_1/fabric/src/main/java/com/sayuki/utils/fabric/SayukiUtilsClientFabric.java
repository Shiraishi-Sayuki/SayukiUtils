/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.fabric;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

import com.sayuki.utils.copyuuid.SayukiCopyUUID;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;
import com.sayuki.utils.eatanim.SayukiEatingAnimation;
import com.sayuki.utils.playtime.PlayTimeClient;
import com.sayuki.utils.playtime.ServerPlayTimeManager;

@Environment(EnvType.CLIENT)
public class SayukiUtilsClientFabric implements ClientModInitializer {
    private boolean wasUsePressed = false;
    private boolean scrollCallbackRegistered = false;

    @Override
    // クライアント初期化
    public void onInitializeClient() {
        SayukiDarkModeClient.init(Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("sayukiutils"));

        registerEatingAnimationPredicates();

        CoreShaderRegistrationCallback.EVENT.register(registrar -> {
            SayukiDarkModeClient.registerAllShaders((location, format, callback) -> {
                try {
                    registrar.register(location, format, callback);
                } catch (java.io.IOException e) {
                }
            });
        });

        ServerPlayTimeManager.load();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            var btn = SayukiDarkModeClient.createDarkModeButton(screen);
            if (btn != null) {
                screen.addRenderableWidget(btn);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            PlayTimeClient.onClientTick();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
            PlayTimeClient.onLeaveServer();
        });

        ClientTickEvents.START_CLIENT_TICK.register(tickClient -> {
            if (tickClient.player == null) return;
            boolean isUsePressed = tickClient.options.keyUse.isDown();
            if (isUsePressed && !wasUsePressed) {
                SayukiCopyUUID.handleUse(tickClient);
            }
            wasUsePressed = isUsePressed;
        });
    }

    // eating animation predicatesを登録
    private static void registerEatingAnimationPredicates() {
        SayukiEatingAnimation.registerPredicates((item, id, predicate) -> 
            FabricModelPredicateProviderRegistry.register(item, id, predicate::call)
        );
    }
}

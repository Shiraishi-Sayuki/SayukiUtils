/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.sayuki.utils.copyuuid.SayukiCopyUUID;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;
import com.sayuki.utils.eatanim.SayukiEatingAnimation;
import com.sayuki.utils.playtime.PlayTimeClient;
import com.sayuki.utils.playtime.ServerPlayTimeManager;

@EventBusSubscriber(modid = "sayukiutils", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SayukiUtilsClientNeoForge {
    private static boolean wasUsePressed = false;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerEatingAnimationPredicates();

        event.enqueueWork(() -> {
            ServerPlayTimeManager.load();

            NeoForge.EVENT_BUS.addListener(SayukiUtilsClientNeoForge::onScreenInit);
            NeoForge.EVENT_BUS.addListener(SayukiUtilsClientNeoForge::onClientTick);
            NeoForge.EVENT_BUS.addListener(SayukiUtilsClientNeoForge::onPlayTimeTick);
            NeoForge.EVENT_BUS.addListener(SayukiUtilsClientNeoForge::onPlayTimeLogout);
        });
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        SayukiDarkModeClient.resetShaderRegistration();
        SayukiDarkModeClient.registerAllShaders((location, format, callback) -> {
            try {
                net.minecraft.client.renderer.ShaderInstance instance = new net.minecraft.client.renderer.ShaderInstance(
                        event.getResourceProvider(), location, format);
                event.registerShader(instance, callback);
            } catch (java.io.IOException e) {
                com.mojang.logging.LogUtils.getLogger().error("Failed to register shader {}", location, e);
            }
        });
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        boolean isUsePressed = client.options.keyUse.isDown();
        if (isUsePressed && !wasUsePressed) {
            SayukiCopyUUID.handleUse(client);
        }
        wasUsePressed = isUsePressed;
    }

    public static void onPlayTimeTick(ClientTickEvent.Post event) {
        PlayTimeClient.onClientTick();
    }

    public static void onPlayTimeLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        PlayTimeClient.onLeaveServer();
    }

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        var btn = SayukiDarkModeClient.createDarkModeButton(event.getScreen());
        if (btn != null) {
            event.addListener(btn);
        }
    }

    private static void registerEatingAnimationPredicates() {
        SayukiEatingAnimation.registerPredicates((item, id, predicate) ->
            ItemProperties.register(item, id, predicate::call)
        );
    }
}

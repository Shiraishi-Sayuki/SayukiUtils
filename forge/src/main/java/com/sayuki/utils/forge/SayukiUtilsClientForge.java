package com.sayuki.utils.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.sayuki.utils.copyuuid.SayukiCopyUUID;
import com.sayuki.utils.darkmode.SayukiDarkModeClient;
import com.sayuki.utils.eatanim.SayukiEatingAnimation;
import com.sayuki.utils.playtime.PlayTimeClient;
import com.sayuki.utils.playtime.ServerPlayTimeManager;

@Mod.EventBusSubscriber(modid = "sayukiutils", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SayukiUtilsClientForge {
    private static boolean wasUsePressed = false;

    @SubscribeEvent
    // クライアントセットアップ処理
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerEatingAnimationPredicates();

        event.enqueueWork(() -> {
            ServerPlayTimeManager.load();

            MinecraftForge.EVENT_BUS.addListener(SayukiUtilsClientForge::onScreenInit);
            MinecraftForge.EVENT_BUS.addListener(SayukiUtilsClientForge::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(SayukiUtilsClientForge::onPlayTimeTick);
            MinecraftForge.EVENT_BUS.addListener(SayukiUtilsClientForge::onPlayTimeLogout);
        });
    }

    @SubscribeEvent
    // シェーダー登録イベント処理
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

    // クライアントティック処理
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        boolean isUsePressed = client.options.keyUse.isDown();
        if (isUsePressed && !wasUsePressed) {
            SayukiCopyUUID.handleUse(client);
        }
        wasUsePressed = isUsePressed;
    }

    // プレイ時間ティック処理
    public static void onPlayTimeTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PlayTimeClient.onClientTick();
    }

    // プレイ時間ログアウト処理
    public static void onPlayTimeLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        PlayTimeClient.onLeaveServer();
    }

    // 画面初期化イベント処理
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        var btn = SayukiDarkModeClient.createDarkModeButton(event.getScreen());
        if (btn != null) {
            event.addListener(btn);
        }
    }

    // eating animation predicatesを登録
    private static void registerEatingAnimationPredicates() {
        SayukiEatingAnimation.registerPredicates((item, id, predicate) ->
            ItemProperties.register(item, id, predicate::call)
        );
    }
}

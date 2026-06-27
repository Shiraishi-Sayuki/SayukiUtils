/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.copyuuid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import com.sayuki.utils.config.ConfigManager;
import com.sayuki.utils.config.CopyUUIDFeatureConfig;

public class SayukiCopyUUID {
    // 何もしない初期化
    public static void init() {
    }

    // 使用処理をハンドリング
    public static boolean handleUse(Minecraft client) {
        if (!ConfigManager.get().get("copyuuid").isEnabled()) return false;
        LocalPlayer player = client.player;
        if (player == null || client.level == null) return false;

        if (!isHoldingTriggerItem(client)) return false;
        if (client.hitResult == null || client.hitResult.getType() != HitResult.Type.ENTITY) return false;

        Entity entity = ((EntityHitResult) client.hitResult).getEntity();
        String uuid = entity.getStringUUID();
        String name = entity.getName().getString();

        client.keyboardHandler.setClipboard(uuid);
        player.displayClientMessage(
                Component.translatable("sayukiutils.copyuuid.copied", name, uuid),
                true
        );
        return true;
    }

    // トリガーアイテムを持ってるかチェック
    private static boolean isHoldingTriggerItem(Minecraft client) {
        CopyUUIDFeatureConfig cfg = ConfigManager.get().get("copyuuid");
        String triggerItemId = cfg.getTriggerItem();
        ItemStack main = client.player.getMainHandItem();
        ItemStack off = client.player.getOffhandItem();
        return main.getItem().builtInRegistryHolder().key().location().toString().equals(triggerItemId)
                || off.getItem().builtInRegistryHolder().key().location().toString().equals(triggerItemId);
    }
}

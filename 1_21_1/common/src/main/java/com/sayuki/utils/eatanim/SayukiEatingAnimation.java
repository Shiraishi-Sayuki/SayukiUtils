/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.eatanim;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.sayuki.utils.config.ConfigManager;
import net.minecraft.core.component.DataComponents;

public class SayukiEatingAnimation {

    @FunctionalInterface
    public interface Predicate {
        // 呼び出し
        float call(ItemStack stack, ClientLevel level, LivingEntity entity, int seed);
    }

    @FunctionalInterface
    public interface Registrar {
        // 登録
        void register(Item item, ResourceLocation id, Predicate predicate);
    }

    // 食べる/飲むアニメーションを登録
    public static void registerPredicates(Registrar registrar) {
        BuiltInRegistries.ITEM.stream().forEach(item -> {
            if (item.components().has(DataComponents.FOOD) || item == Items.MILK_BUCKET) {
                registrar.register(item, ResourceLocation.withDefaultNamespace("eat"), checkEnabled(eatProgress()));
                registrar.register(item, ResourceLocation.withDefaultNamespace("eating"), checkEnabled(isEating()));
            }
        });

        registerDrinkPredicates(registrar, Items.POTION);
        registerDrinkPredicates(registrar, Items.MILK_BUCKET);
        registerDrinkPredicates(registrar, Items.HONEY_BOTTLE);
    }

    // 飲むアニメーションを登録
    private static void registerDrinkPredicates(Registrar r, Item item) {
        r.register(item, ResourceLocation.withDefaultNamespace("drink"), checkEnabled(drinkProgress()));
        r.register(item, ResourceLocation.withDefaultNamespace("drinking"), checkEnabled(isDrinking()));
    }

    // 機能が有効ならdelegateを実行
    private static Predicate checkEnabled(Predicate delegate) {
        return (stack, level, entity, seed) -> {
            if (!ConfigManager.get().get("eatanim").isEnabled()) return 0.0F;
            return delegate.call(stack, level, entity, seed);
        };
    }

    // 食べ進捗を返すpredicate
    public static Predicate eatProgress() {
        return (stack, level, entity, seed) -> {
            if (entity == null) return 0.0F;
            return entity.getUseItem() != stack ? 0.0F : (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 30.0F;
        };
    }

    // 食べてるか返すpredicate
    public static Predicate isEating() {
        return (stack, level, entity, seed) -> {
            if (entity == null) return 0.0F;
            return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        };
    }

    // 飲み進捗を返すpredicate
    public static Predicate drinkProgress() {
        return (stack, level, entity, seed) -> {
            if (entity == null) return 0.0F;
            return entity.getUseItem() != stack ? 0.0F : (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 30.0F;
        };
    }

    // 飲んでるか返すpredicate
    public static Predicate isDrinking() {
        return (stack, level, entity, seed) -> {
            if (entity == null) return 0.0F;
            return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        };
    }
}

package com.sayuki.utils.lan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.players.StoredUserList;

@Mixin(StoredUserList.class)
public interface StoredUserListAccessor<K> {
    @Invoker("contains")
    // containsメソッドへのインボーカー
    boolean callContains(K object);
}

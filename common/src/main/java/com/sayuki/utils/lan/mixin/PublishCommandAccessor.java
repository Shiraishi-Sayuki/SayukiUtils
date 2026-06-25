package com.sayuki.utils.lan.mixin;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.commands.PublishCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PublishCommand.class)
public interface PublishCommandAccessor {
    @Accessor("ERROR_FAILED")
    // ERROR_FAILEDフィールドへのアクセサ
    SimpleCommandExceptionType getErrorFailed();
}

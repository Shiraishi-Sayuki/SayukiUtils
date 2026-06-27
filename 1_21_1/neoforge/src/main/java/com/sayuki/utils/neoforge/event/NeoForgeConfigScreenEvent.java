/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.neoforge.event;

import java.util.function.Supplier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import com.sayuki.utils.client.config.ConfigScreen;

public class NeoForgeConfigScreenEvent {
    public static void init() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        container.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) () -> (con, screen) -> ConfigScreen.create(screen));
    }
}

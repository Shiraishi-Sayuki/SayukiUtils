/*
 * SayukiUtils
 * Copyright (C) 2026 Shiraishi Sayuki. <sayukishiraishi@gmail.com>
 */

package com.sayuki.utils.playtime.mixin;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFixer;
import com.sayuki.utils.playtime.util.IWithPlayTime;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
    @Inject(at = @At("RETURN"), method = {"m_304621_", "m_230815_", "method_43418", "lambda$loadLevelSummaries$0"}, remap = false)
    // LevelSummary取得後の処理
    public void onReturnLevelSummary(LevelStorageSource.LevelDirectory levelDirectory, CallbackInfoReturnable<LevelSummary> cir) {
        LevelSummary levelSummary = cir.getReturnValue();

        if (levelSummary instanceof IWithPlayTime withPlayTime) {
            Path directoryPath = levelDirectory.path();
            Path stats = directoryPath.getParent().resolve("stats");
            File statsFile = stats.toFile();

            if (statsFile.isDirectory()) {
                File[] saveFiles = statsFile.listFiles();

                if (saveFiles != null) {
                    int totalPlayTime = 0;
                    for (File file : saveFiles) {
                        try (FileReader fileReader = new FileReader(file)) {
                            JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
                            if (jsonObject.has("stats")) {
                                JsonObject statsObject = jsonObject.getAsJsonObject("stats");
                                if (statsObject.has("minecraft:custom")) {
                                    JsonObject customObject = statsObject.getAsJsonObject("minecraft:custom");
                                    if (customObject.has("minecraft:play_time")) {
                                        totalPlayTime += customObject.get("minecraft:play_time").getAsInt();
                                    }
                                }
                            }
                        } catch (JsonIOException | IOException | IllegalStateException ignored) {}
                    }

                    if (totalPlayTime > 0) {
                        withPlayTime.setPlayTimeTicks(totalPlayTime);
                    }
                }
            }
        }
    }
}

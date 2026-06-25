package com.sayuki.utils.tooltip.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import com.sayuki.utils.tooltip.TooltipHelper;

@Mixin(GuiGraphics.class)
public class TooltipMixin {

    @ModifyVariable(method = "drawTooltip", at = @At("HEAD"), index = 2, argsOnly = true)
    // ツールチップコンポーネントを修正
    public List<ClientTooltipComponent> sayuki$modifyComponents(List<ClientTooltipComponent> components, Font font, List<ClientTooltipComponent> list, int x) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        return TooltipHelper.fix(components, font, x, screenWidth);
    }

    @ModifyVariable(method = "drawTooltip", at = @At("HEAD"), index = 3, argsOnly = true)
    // ツールチップX座標を修正
    public int sayuki$modifyX(int x) {
        return x + TooltipHelper.getFlipOffset();
    }
}

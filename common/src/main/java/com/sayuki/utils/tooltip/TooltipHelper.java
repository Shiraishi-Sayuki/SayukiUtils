package com.sayuki.utils.tooltip;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

import com.sayuki.utils.tooltip.mixin.OrderedTextToolTipAccessor;

public class TooltipHelper {
    private static int flipOffset = 0;

    // ツールチップの位置と幅を調整
    public static List<ClientTooltipComponent> fix(List<ClientTooltipComponent> components, Font font, int x, int screenWidth) {
        flipOffset = 0;

        List<ClientTooltipComponent> result = new ArrayList<>(components);
        if (result.isEmpty()) return result;

        int availableRight = screenWidth - x - 20;
        int forcedWidth = 0;
        for (ClientTooltipComponent c : result) {
            if (!(c instanceof ClientTextTooltip)) {
                int w = c.getWidth(font);
                if (w > forcedWidth) forcedWidth = w;
            }
        }

        boolean needsFlip = false;
        int wrapWidth;
        if (forcedWidth > availableRight || availableRight < 100) {
            needsFlip = true;
            wrapWidth = Math.max(x - 28, 80);
        } else {
            wrapWidth = Math.max(availableRight, 80);
        }

        wrapLongLines(result, font, wrapWidth);

        if (needsFlip) {
            flipOffset = -(28 + maxWidthOf(result, font));
        }

        return result;
    }

    // 反転オフセットを取得
    public static int getFlipOffset() {
        return flipOffset;
    }

    // ツールチップをラップ
    public static void wrap(List<ClientTooltipComponent> components, Font font, int x, int screenWidth) {
        List<ClientTooltipComponent> wrapped = fix(components, font, x, screenWidth);
        if (wrapped != components) {
            try {
                components.clear();
                components.addAll(wrapped);
            } catch (UnsupportedOperationException ignored) {
            }
        }
    }

    // 最大幅を計算
    private static int maxWidthOf(List<ClientTooltipComponent> components, Font font) {
        int maxWidth = 0;
        for (ClientTooltipComponent c : components) {
            int w = c.getWidth(font);
            if (w > maxWidth) maxWidth = w;
        }
        return maxWidth;
    }

    // 長い行を折り返し
    private static void wrapLongLines(List<ClientTooltipComponent> components, Font font, int maxSize) {
        if (maxSize <= 0) return;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent c = components.get(i);
            if (c instanceof ClientTextTooltip ttc) {
                FormattedCharSequence text = ((OrderedTextToolTipAccessor) ttc).getText();
                Component asText = formattedCharSequenceToComponent(text);
                if (asText == null || asText.getSiblings().isEmpty()) continue;

                List<FormattedCharSequence> wrapped = font.split(asText, maxSize);
                List<ClientTooltipComponent> wrappedComponents = new ArrayList<>();
                for (FormattedCharSequence wt : wrapped) {
                    wrappedComponents.add(ClientTooltipComponent.create(wt));
                }
                components.remove(i);
                components.addAll(i, wrappedComponents);
                i += wrappedComponents.size() - 1;
            }
        }
    }

    // FormattedCharSequenceをComponentに変換
    private static Component formattedCharSequenceToComponent(FormattedCharSequence sequence) {
        ComponentCollector visitor = new ComponentCollector();
        sequence.accept(visitor);
        return visitor.getText();
    }

    private static class ComponentCollector implements FormattedCharSink {
        private final MutableComponent text = Component.empty();

        @Override
        // 文字をComponentに追加
        public boolean accept(int index, Style style, int codePoint) {
            text.append(Component.literal(new String(Character.toChars(codePoint))).withStyle(style));
            return true;
        }

        // テキストを取得
        public Component getText() {
            return text;
        }
    }
}

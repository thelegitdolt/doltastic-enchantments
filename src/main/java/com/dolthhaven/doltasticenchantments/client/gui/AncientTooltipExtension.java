package com.dolthhaven.doltasticenchantments.client.gui;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.AncientBookNode;
import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import me.alfie.immersiveenchanting.api.DescriptionLayoutExtension;
import me.alfie.immersiveenchanting.api.internal.cost.LevelsDescriptionLine;
import me.alfie.immersiveenchanting.api.internal.cost.MaterialsDescriptionLine;
import me.alfie.immersiveenchanting.config.ClientConfig;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.Node;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeTooltip;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.tooltip.DescriptionLayout;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.tooltip.DescriptionLine;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Code modified from Alfie Immersive Enchanting mod, used under MIT license
 */
public class AncientTooltipExtension implements DescriptionLayoutExtension {
    @Override
    public void extendLayout(DescriptionLayout description, NodeTooltip parentTooltip) {
        if (parentTooltip instanceof AncientBookNode.AncientNodeTooltips tooltip) {
            Node textNode = parentTooltip.node;
            if (textNode instanceof AncientBookNode ancientNode) {
                if (ancientNode.isUnlocked() && ancientNode.getEnchantment() != null) {
                    tooltip.setCurrentRenderedCost(NodeTooltip.getCycledElement(tooltip.getValidCosts(), ClientConfig.getItemCarouselSpeed()));

                    description.insertLine(0, new MaterialsDescriptionLine(tooltip));
                    if (tooltip.getCurrentRenderedCost().xpLevels() > 0) {
                        description.insertLine(2, new LevelsDescriptionLine(tooltip));
                    }
                } else {
                   displayMultiline(description, "gui.%s.conjuring_hint".formatted(DoltasticEnchantments.MOD_ID));
                }
            }
        }
    }

    /**
     * Code by Alfie Immersive Enchanting mod, used under MIT license
     */
    private static void displayMultiline(DescriptionLayout description, String translatable) {
        String text = Component.translatable(translatable).getString();

        final List<String> textChunks = DescriptionLayout.chunkString(text, 32);

        for (int i = 0; i < textChunks.size(); i += 1) {
            final int index = i;
            description.insertLine(index, new DescriptionLine() {
                public void draw(GuiGraphics graphics, int lineX, int lineY) {
                    graphics.drawString(Minecraft.getInstance().font, this.getText(), lineX, lineY, 16777215);
                }

                public @NotNull Component getText() {
                    Component label = Component.literal(textChunks.get(index));

                    return label.copy().withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC);
                }
            });
        }
    }
}

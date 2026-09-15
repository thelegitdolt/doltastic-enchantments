package com.dolthhaven.doltasticenchantments.common.enchnting.tooltip;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.ConjureNodeData;
import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import me.alfie.immersiveenchanting.api.description.DescriptionHelper;
import me.alfie.immersiveenchanting.api.description.DescriptionLayout;
import me.alfie.immersiveenchanting.api.description.DescriptionLayoutExtension;
import me.alfie.immersiveenchanting.gui.tab.enchanting.node.NodeState;
import me.alfie.immersiveenchanting.gui.tab.enchanting.tooltip.NodeTooltip;
import net.minecraft.ChatFormatting;

public class ConjureLayoutExtension implements DescriptionLayoutExtension {
    @Override
    public void extendLayout(DescriptionLayout description, NodeTooltip tooltip) {
        if (!tooltip.node().branchId().equals(ConjureNodeData.TYPE)) return;

        description.widthPadding = 16;
        int linesCreated = 0;
        if (tooltip.node().isState(NodeState.UNOBTAINED)) {
        } else if (tooltip.node().isState(NodeState.LOCKED)) {
            linesCreated = DescriptionHelper.lineWrapComponent(
                    DoltasticEnchantments.translatable("gui.%s.tooltip.desc.conjure_hint")
                            .withStyle(ChatFormatting.GRAY),
                    DescriptionHelper.DEFAULT_LINE_WIDTH, description, 0);
        }

        if (tooltip.node().isState(NodeState.UNOBTAINED)) {
            DescriptionHelper.insertCostLines(tooltip, description, linesCreated + 1);
        }
    }
}

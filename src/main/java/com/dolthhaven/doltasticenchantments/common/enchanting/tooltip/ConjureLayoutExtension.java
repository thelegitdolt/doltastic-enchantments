package com.dolthhaven.doltasticenchantments.common.enchanting.tooltip;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.ConjureNodeData;
import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import me.alfie.immersiveenchanting.api.description.DescriptionHelper;
import me.alfie.immersiveenchanting.api.description.DescriptionLayout;
import me.alfie.immersiveenchanting.api.description.DescriptionLayoutExtension;
import me.alfie.immersiveenchanting.api.description.internal.lines.LevelsLine;
import me.alfie.immersiveenchanting.api.description.internal.lines.MaterialsLine;
import me.alfie.immersiveenchanting.gui.tab.enchanting.node.NodeState;
import me.alfie.immersiveenchanting.gui.tab.enchanting.tooltip.NodeTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Items;

import static me.alfie.immersiveenchanting.api.description.DescriptionHelper.lineWrapComponent;

public class ConjureLayoutExtension implements DescriptionLayoutExtension {
    @Override
    public void extendLayout(DescriptionLayout description, NodeTooltip tooltip) {
        if (!tooltip.node().data().type().equals(ConjureNodeData.TYPE)) return;

        description.widthPadding = 16;
        int linesCreated = 0;
        if (tooltip.node().isState(NodeState.UNOBTAINED)) {

        } else if (tooltip.node().isState(NodeState.LOCKED)) {
            linesCreated = lineWrapComponent(
                    DoltasticEnchantments.translatable("gui.%s.tooltip.desc.conjure_hint")
                            .withStyle(ChatFormatting.GRAY),
                    DescriptionHelper.DEFAULT_LINE_WIDTH, description, 0);
        }

        if (tooltip.node().isState(NodeState.UNOBTAINED)) {
            renderConjureCosts(description, tooltip, linesCreated);
        }
    }

    private static void renderConjureCosts(DescriptionLayout description, NodeTooltip tooltip, int lineStart) {
        int lineNumber = lineStart;
        if (!tooltip.screen().enchantmentCostRenderer().getCurrentRenderedCost().stack().is(Items.AIR)) {
            description.insertLine(lineStart, new MaterialsLine(tooltip));
            lineNumber = lineStart + 2;
        }

        if (tooltip.screen().enchantmentCostRenderer().getCurrentRenderedCost().xpLevels() > 0) {
            description.insertLine(lineNumber, new LevelsLine(tooltip));
        }
    }
}

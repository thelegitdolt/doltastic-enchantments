package com.dolthhaven.doltasticenchantments.core.events;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.ConjureNodeData;
import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import me.alfie.immersiveenchanting.api.node.BranchBuilder;
import me.alfie.immersiveenchanting.api.node.BuildBranchesEvent;
import me.alfie.immersiveenchanting.api.node.NodeTemplate;
import me.alfie.immersiveenchanting.api.node.SpriteIcon;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.CostRegistry;
import me.alfie.immersiveenchanting.gui.tab.enchanting.node.NodeState;
import me.alfie.immersiveenchanting.gui.tab.enchanting.node.NodeTier;
import me.alfie.immersiveenchanting.util.EnchantmentTextureHelper;
import net.minecraft.world.item.Items;

public class EnchantingMenuEvents {
    public static void addDoltBranches(BuildBranchesEvent event) {
        if (event.getStack().is(Items.EMERALD)) {
            buildConjureBranch(event);
        }
    }

    private static void buildConjureBranch(BuildBranchesEvent event) {
        NodeTemplate conjureNode = new NodeTemplate(DoltasticEnchantments.translatable("gui.%s.tooltip.title.conjure"), 0,
                NodeState.OBTAINED, NodeTier.ADVANCED,
                new SpriteIcon(EnchantmentTextureHelper.getTexture(CostRegistry.TRANSMUTE)), ConjureNodeData.create());
        event.addBranch(BranchBuilder.of(event.getCanvas(), CostRegistry.TRANSMUTE).node(conjureNode).build());
    }
}

package com.dolthhaven.doltasticenchantments.common.enchanting;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.ConjureNodeData;
import com.dolthhaven.doltasticenchantments.common.enchnting.tooltip.ConjureLayoutExtension;
import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.api.description.RegisterDescriptionLayoutEvent;
import me.alfie.immersiveenchanting.api.node.BranchBuilder;
import me.alfie.immersiveenchanting.api.node.BuildBranchesEvent;
import me.alfie.immersiveenchanting.api.node.NodeTemplate;
import me.alfie.immersiveenchanting.api.node.SpriteIcon;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.CostRegistry;
import me.alfie.immersiveenchanting.gui.tab.enchanting.node.NodeState;
import me.alfie.immersiveenchanting.gui.tab.enchanting.node.NodeTier;
import me.alfie.immersiveenchanting.util.EnchantmentTextureHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import static java.util.Objects.isNull;

public class EnchantingMenuEvents {
    public static void addDoltBranches(BuildBranchesEvent event) {
        if (event.getStack().is(Items.EMERALD)) {

            buildConjureBranch(event);
        }
    }

    private static void buildConjureBranch(BuildBranchesEvent event) {
        ItemStack costItem = event.getCanvas().screen().getMenu().getCostSlot().getItem();
        Holder<Enchantment> holder = ReagentsRegistry.client().getValue(costItem);
        SpriteIcon iconSprite = new SpriteIcon(EnchantmentTextureHelper.getTexture(isNull(holder) ? CostRegistry.REPLICATE : ResourceId.parse(holder.getRegisteredName())));

        NodeState nodeState = isNull(holder) ? NodeState.LOCKED : NodeState.UNOBTAINED;
        Component component = isNull(holder) ? DoltasticEnchantments.translatable("gui.%s.tooltip.title.conjure") :
                holder.value().description();

        NodeTemplate conjureNode = new NodeTemplate(
                component, 0,
                nodeState, NodeTier.BASIC, iconSprite, ConjureNodeData.create());
        event.addBranch(BranchBuilder.of(event.getCanvas(), ReagentsRegistry.CONJURE_ID).node(conjureNode).build());
    }

    public static void registerNodeExtensions(RegisterDescriptionLayoutEvent event) {
        event.register(new ConjureLayoutExtension());
    }
}

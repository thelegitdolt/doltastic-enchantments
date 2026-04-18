package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.datapack.ReagentStackHolder;
import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.EnchantingTab;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.BranchFactory;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.Node;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeBranch;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.enchanting.EnchantingNode;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.enchanting.EnchantingNodeBranch;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReagentStackUtil {
    public static void checkReagentSlotUpdated(EnchantingTableScreen screen, ReagentStackHolder holder) {
        ItemStack stack = screen.getMenu().getSlot(EnchantingTableMenu.SLOTS.COST.ordinal()).getItem();
        if (!ItemStack.isSameItemSameTags(holder.lastReagentStack(), stack)) {
            holder.setReagentStack(stack.copy());
            bookEnchantingUpdate(screen, screen.enchantingTab, holder.lastReagentStack());
        }
    }

    public static boolean bookEnchantingUpdate(EnchantingTableScreen screen, EnchantingTab tab, ItemStack reagentStack) {
        ItemStack stack = screen.getMenu().getSlot(EnchantingTableMenu.SLOTS.TOOL.ordinal()).getItem();
        tab.branches.clear();
        tab.getRenderedNodes().clear();
        tab.setLockHover(false);

        if (!stack.is(Items.BOOK) || reagentStack.isEmpty()) {
            return false;
        }

        List<Holder<Enchantment>> applicableEnchantments = getApplicableEnchants(reagentStack, screen.player.level());

        List<Float> angles = BranchFactory.generateBranchAngles(applicableEnchantments.size());
        int i = 0;

        for (Holder<Enchantment> enchantmentHolder : applicableEnchantments) {
            tab.branches.add(new EnchantingNodeBranch(screen, angles.get(i), enchantmentHolder, 0, true, screen.player));
            ++i;
        }
        NodeBranch.calculateNodeAnglesAndStep(screen);

        for (NodeBranch branch : tab.branches) {
            branch.placeNodesAlongLine();

            for (Node node : branch.getNodes()) {
                node.setScale(EnchantingNode.globalScale);
            }
        }

        return true;
    }

    private static List<Holder<Enchantment>> getApplicableEnchants(ItemStack stack, Level level) {
        ItemStack bareStack = new ItemStack(stack.getItem());
        List<Holder.Reference<Enchantment>> enchants =  EnchantmentUtil.getAllEnchantments(level).stream()
                .filter(enchant -> enchant.get().canEnchant(bareStack))
                .sorted(Comparator.comparing((holder) -> holder.unwrapKey().orElseThrow().location().toString())).toList();
        return new ArrayList<>(enchants);
    }
}

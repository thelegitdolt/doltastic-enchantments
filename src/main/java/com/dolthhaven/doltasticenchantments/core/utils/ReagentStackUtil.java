package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.AncientBookBranch;
import com.dolthhaven.doltasticenchantments.core.datapack.ReagentStackHolder;
import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.tab.enchanting.EnchantingTab;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import me.alfie.immersiveenchanting.util.FxHelper;
import net.minecraft.core.Holder;
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
        if (!stack.is(Items.BOOK)) {
            return false;
        }
        tab.branches.clear();
        tab.getRenderedNodes().clear();
        tab.setLockHover(false);

        FxHelper.playEnchantingTableToolSlotSound(screen.player);
        screen.getCanvas().setDraggingEnabled(true);
        if (!tryAddEnchantNodes(reagentStack, screen, tab)) {
            addNoReagentBookHint(screen, tab);
        }
        return true;
    }

    private static void addNoReagentBookHint(EnchantingTableScreen screen, EnchantingTab tab) {
        tab.branches.add(new AncientBookBranch(screen, 0, null, false));
        adjustBranches(screen, tab);
    }

    private static boolean tryAddEnchantNodes(ItemStack reagentStack, EnchantingTableScreen screen, EnchantingTab tab) {
        List<Holder<Enchantment>> applicableEnchantments = getApplicableEnchants(reagentStack, screen.player.level());

        if (applicableEnchantments.isEmpty())
            return false;

        List<Float> angles = BranchFactory.generateBranchAngles(applicableEnchantments.size());
        int i = 0;

        for (Holder<Enchantment> enchantmentHolder : applicableEnchantments) {
            tab.branches.add(new AncientBookBranch(screen, angles.get(i), enchantmentHolder, true));
            ++i;
        }
        adjustBranches(screen, tab);
        return true;
    }

    private static void adjustBranches(EnchantingTableScreen screen, EnchantingTab tab) {
        NodeBranch.calculateNodeAnglesAndStep(screen);

        for (NodeBranch branch : tab.branches) {
            branch.placeNodesAlongLine();

            for (Node node : branch.getNodes()) {
                node.setScale(EnchantingNode.globalScale);
            }
        }
    }

    private static List<Holder<Enchantment>> getApplicableEnchants(ItemStack stack, Level level) {
        ItemStack bareStack = new ItemStack(stack.getItem());
        List<Holder.Reference<Enchantment>> enchants =  EnchantmentUtil.getAllEnchantments(level).stream()
                .filter(enchant -> enchant.get().canEnchant(bareStack) &&
                        EnchantCostUtil.requiresBook(level, enchant.unwrapKey().orElseThrow()))
                .sorted(Comparator.comparing((holder) -> holder.unwrapKey().orElseThrow().location().toString())).toList();
        return new ArrayList<>(enchants);
    }
}

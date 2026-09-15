package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.common.enchanting.ReagentStackHolder;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import net.minecraft.world.item.ItemStack;

public class ReagentStackUtil {
    public static void checkReagentSlotUpdated(EnchantingTableScreen screen, ReagentStackHolder holder, ItemStack stack) {
        if (!ItemStack.isSameItemSameComponents(holder.lastReagentStack(), stack)) {
            onCostSlotUpdate(screen);
            holder.setReagentStack(stack.copy());
        }
    }

    public static void onCostSlotUpdate(EnchantingTableScreen screen) {
        screen.rebuildBranches();
    }

//    private static boolean tryAddEnchantNodes(ItemStack reagentStack, EnchantingTableScreen screen, EnchantingTab tab) {
//        List<Holder<Enchantment>> applicableEnchantments = getApplicableEnchants(reagentStack, screen.player.level());
//
//        if (applicableEnchantments.isEmpty())
//            return false;
//
//        List<Float> angles = BranchFactory.generateBranchAngles(applicableEnchantments.size());
//        int i = 0;
//
//        for (Holder<Enchantment> enchantmentHolder : applicableEnchantments) {
//            tab.branches.add(new AncientBookBranch(screen, angles.get(i), enchantmentHolder, true));
//            ++i;
//        }
//        adjustBranches(screen, tab);
//        return true;
//    }
//
//    private static void adjustBranches(EnchantingTableScreen screen, EnchantingTab tab) {
//        NodeBranch.calculateNodeAnglesAndStep(screen);
//
//        for (NodeBranch branch : tab.branches) {
//            branch.placeNodesAlongLine();
//
//            for (Node node : branch.getNodes()) {
//                node.setScale(EnchantingNode.globalScale);
//            }
//        }
//    }
//
//    private static List<Holder<Enchantment>> getApplicableEnchants(ItemStack stack, Level level) {
//        ItemStack bareStack = new ItemStack(stack.getItem());
//        List<Holder.Reference<Enchantment>> enchants =  EnchantmentUtil.getAllEnchantments(level).stream()
//                .filter(enchant -> enchant.get().canEnchant(bareStack) &&
//                        EnchantCostUtil.requiresBook(level, enchant.unwrapKey().orElseThrow()))
//                .sorted(Comparator.comparing((holder) -> holder.unwrapKey().orElseThrow().location().toString())).toList();
//        return new ArrayList<>(enchants);
//    }
}

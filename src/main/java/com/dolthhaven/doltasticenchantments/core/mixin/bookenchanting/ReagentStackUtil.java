package com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting;

import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.EnchantingTab;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.BranchFactory;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.enchanting.EnchantingNodeBranch;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class ReagentStackUtil {
    public void checkReagentSlotUpdated(EnchantingTableScreen screen, ReagentStackHolder holder) {
        ItemStack stack = screen.getMenu().getSlot(EnchantingTableMenu.SLOTS.COST.ordinal()).getItem();
        if (!ItemStack.isSameItemSameTags(holder.lastReagentStack(), stack)) {
            holder.setReagentStack(stack.copy());
            onReagentSlotUpdate(screen, screen.enchantingTab);
        }
    }

    public void onReagentSlotUpdate(EnchantingTableScreen screen, EnchantingTab tab) {
        ItemStack stack = screen.getMenu().getSlot(EnchantingTableMenu.SLOTS.TOOL.ordinal()).getItem();
        if (!stack.is(Items.BOOK)) {
            return;
        }
        tab.branches.clear();
        tab.getRenderedNodes().clear();
        tab.setLockHover(false);

        List<Holder<Enchantment>> applicableEnchantments = getApplicableEnchants(stack);

        List<Float> angles = BranchFactory.generateBranchAngles(applicableEnchantments.size());
        int i = 0;

        for (Holder<Enchantment> enchantmentHolder : applicableEnchantments) {
            tab.branches.add(new EnchantingNodeBranch(screen, angles.get(i), enchantmentHolder, 0, true, screen.player));
            ++i;
        }
    }

    private List<Holder<Enchantment>> getApplicableEnchants(ItemStack stack) {
        return List.of();
    }
}

package com.dolthhaven.doltasticenchantments.common.enchanting;

import net.minecraft.world.item.ItemStack;

public interface ReagentStackHolder {
    ItemStack lastReagentStack();

    void setReagentStack(ItemStack stack);
}

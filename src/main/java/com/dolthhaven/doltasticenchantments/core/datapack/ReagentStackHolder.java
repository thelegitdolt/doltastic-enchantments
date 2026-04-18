package com.dolthhaven.doltasticenchantments.core.datapack;

import net.minecraft.world.item.ItemStack;

public interface ReagentStackHolder {
    ItemStack lastReagentStack();

    void setReagentStack(ItemStack stack);
}

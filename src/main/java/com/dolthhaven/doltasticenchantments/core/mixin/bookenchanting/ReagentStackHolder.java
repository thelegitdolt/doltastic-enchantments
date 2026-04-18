package com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting;

import net.minecraft.world.item.ItemStack;

public interface ReagentStackHolder {
    ItemStack lastReagentStack();

    void setReagentStack(ItemStack stack);
}

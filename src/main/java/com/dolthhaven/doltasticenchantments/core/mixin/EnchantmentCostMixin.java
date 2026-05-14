package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EnchantmentCost.class)
public class EnchantmentCostMixin implements DefaultEnchantmentHolder {
    @Unique private boolean requireBook = true;

    @Override
    public boolean requiresBook() {
        return requireBook;
    }

    @Override
    public void setRequiresBook(boolean isDefault) {
        requireBook = isDefault;
    }
}

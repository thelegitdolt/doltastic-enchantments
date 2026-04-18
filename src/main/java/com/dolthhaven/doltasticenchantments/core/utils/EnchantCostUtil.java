package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;

public class EnchantCostUtil {
    public static boolean requiresBook(EnchantmentCost cost) {
        return (cost instanceof DefaultEnchantmentHolder holder && holder.requiresBook());
    }
}

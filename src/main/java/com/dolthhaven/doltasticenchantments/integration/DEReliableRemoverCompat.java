package com.dolthhaven.doltasticenchantments.integration;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public class DEReliableRemoverCompat {
    public static boolean isEnchantmentRemoved(Holder<Enchantment> enchantment) {
        return Reliable.isEnchantmentBlocked(enchantment);
    }
}

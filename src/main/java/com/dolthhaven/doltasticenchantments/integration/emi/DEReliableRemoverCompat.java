package com.dolthhaven.doltasticenchantments.integration.emi;

import com.evandev.reliable_remover.api.ReliableRemoverAPI;
import net.minecraft.world.item.enchantment.Enchantment;

public class DEReliableRemoverCompat {
    public static boolean isEnchantmentRemoved(Enchantment enchantment) {
        return ReliableRemoverAPI.isEnchantmentBlocked(enchantment);
    }
}

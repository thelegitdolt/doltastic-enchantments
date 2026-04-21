package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class EnchantCostUtil {
    public static boolean requiresBook(EnchantmentCost cost) {
        return (cost instanceof DefaultEnchantmentHolder holder && holder.requiresBook());
    }

    public static boolean requiresBook(Level level, ResourceKey<Enchantment> enchantKey) {
        return requiresBook(EnchantmentCostRegistry.getRegistry(level).getEnchantmentCost(enchantKey));
    }
}

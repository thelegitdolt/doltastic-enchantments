package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
import me.alfie.immersiveenchanting.datapack.cost.CostEntry;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class EnchantCostUtil {
    public static boolean requiresBook(EnchantmentCost cost) {
        return (cost instanceof DefaultEnchantmentHolder holder && holder.requiresBook());
    }

    public static boolean requiresBook(Level level, ResourceKey<Enchantment> enchantKey) {
        return requiresBook(EnchantmentCostRegistry.getRegistry(level).getEnchantmentCost(enchantKey));
    }

    public static <E> String reduceToString(Iterable<E> list, Function<E, ?> stringFunction, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Iterator<E> iterator = list.iterator(); iterator.hasNext();) {
            stringBuilder.append(stringFunction.apply(iterator.next()).toString());
            if (iterator.hasNext()) {
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }

    public static CostEntry basicCost(String item, int xp) {
        return new CostEntry(item, "", 1, xp);
    }
}

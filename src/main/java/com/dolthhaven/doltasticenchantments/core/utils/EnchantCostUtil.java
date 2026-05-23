package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
import me.alfie.immersiveenchanting.datapack.cost.CostDefinition;
import me.alfie.immersiveenchanting.datapack.cost.CostEntry;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Function;

public class EnchantCostUtil {
    public static final String REQUIRES_BOOK = "requiresBook";
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

    public static EnchantmentCost createFairyDustCosts(boolean enabled, List<Integer> defaultCosts, boolean unlockedByDefault) {
        Map<String, CostDefinition> levelCosts = new HashMap<>();

        for (int i = 1; i <= defaultCosts.size(); i++) {
            int amount = defaultCosts.get(i - 1);
            CostEntry costEntry = amount == 0 ? CostEntry.EMPTY : new CostEntry(BuiltInRegistries.ITEM.getKey(DEItems.FAIRY_DUST.get()).toString(), "", amount,0);
            levelCosts.put(String.valueOf(i), costEntry);
        }
        EnchantmentCost cost = new EnchantmentCost(levelCosts, enabled);
        ((DefaultEnchantmentHolder) cost).setRequiresBook(unlockedByDefault);
        return cost;
    }

    public static List<Integer> defaultCosts(int maxLevel) {
        if (maxLevel == 1) return List.of(4);
        List<Integer> cost = new ArrayList<>(maxLevel);
        for (int i = 0; i < maxLevel; i++) {
            cost.add(i + 2);
        }
        return cost;
    }

    public static CostEntry basicCost(String item, int xp) {
        return new CostEntry(item, "", 1, xp);
    }
}

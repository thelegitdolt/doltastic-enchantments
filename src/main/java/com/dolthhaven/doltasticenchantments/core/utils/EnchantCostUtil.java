package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import me.alfie.alfinolib.util.codec.ItemCost;
import me.alfie.alfinolib.util.codec.ItemCostIngredient;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.Cost;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostData;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostLevels;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class EnchantCostUtil {
    public static int[][] DEFAULT_COUNT = {
            {}, {5}, {3, 5}, {2, 3, 5}, {1, 2, 4, 5}, {1, 2, 3, 4, 5}
    };
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

    public static CostData defaultCost(int maxLevel) {
        int[] defaultCount = DEFAULT_COUNT[maxLevel];
        assert defaultCount.length == maxLevel;

        return new CostData(true, new CostLevels(Util.make(new HashMap<>(), map -> {
            for (int i = 1; i <= maxLevel; i++) {
                map.put(i, new CostHolder(List.of(
                        new Cost(
                                new ItemCost(
                                        new ItemCostIngredient.SingleItem(DEItems.FAIRY_DUST.get()),
                                        defaultCount[i - 1]
                                ),
                                Math.min(defaultCount[i - 1] - 2, 0)
                        )
                )));
            }
        })));
    }
//
//    public static EnchantmentCost createFairyDustCosts(boolean enabled, List<Integer> defaultCosts, List<Integer> defaultLevels, boolean unlockedByDefault) {
//        Map<String, CostDefinition> levelCosts = new HashMap<>();
//
//        for (int i = 1; i <= defaultCosts.size(); i++) {
//            int amount = defaultCosts.get(i - 1);
//            int level = defaultLevels.get(i - 1);
//            CostEntry costEntry = new CostEntry(amount == 0 ? "minecraft:air" : BuiltInRegistries.ITEM.getKey(DEItems.FAIRY_DUST.get()).toString(), "", amount, level);
//            levelCosts.put(String.valueOf(i), costEntry);
//        }
//        EnchantmentCost cost = new EnchantmentCost(levelCosts, enabled);
//        ((DefaultEnchantmentHolder) cost).setRequiresBook(unlockedByDefault);
//        return cost;
//    }
//
//    public static List<Integer> defaultCosts(int maxLevel) {
//        if (maxLevel == 1) return List.of(4);
//        List<Integer> cost = new ArrayList<>(maxLevel);
//        for (int i = 0; i < maxLevel; i++) {
//            cost.add(i + 2);
//        }
//        return cost;
//    }

    public static Cost singleItem(Item item, int xpCost) {
        return new Cost(new ItemCost(new ItemCostIngredient.SingleItem(item)), xpCost);
    }

    public static Cost multipleItems(List<Item> items, int xpCost) {
        return new Cost(new ItemCost(new ItemCostIngredient.ItemList(items)), xpCost);
    }

    public static Cost tag(TagKey<Item> tag, int xpCost) {
        return new Cost(new ItemCost(new ItemCostIngredient.TagIngredient(tag)), xpCost);
    }
}

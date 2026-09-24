package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.alfie.alfinolib.util.codec.ItemCost;
import me.alfie.alfinolib.util.codec.ItemCostIngredient;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.Cost;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostData;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostLevels;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class EnchantCostUtil {
    public static Codec<CostHolder> FROM_STRING_LIST_CODEC = Codec.STRING.listOf().comapFlatMap(stringList -> {
        var result = ummmList(stringList);
        if (result.getSecond().isEmpty()) {
            return DataResult.success(result.getFirst());
        } else {
            return DataResult.error(() -> "Could not fully compile, found unparsable strings %s"
                    .formatted(reduceToString(result.getSecond(), a -> a, ", ")));
        }
    }, costHolder -> EnchantCostUtil
            .map(costHolder, item -> BuiltInRegistries.ITEM.getKey(item).toString(), tag -> tag.location().toString()));

    public static final int CONJURE_XP_COST = 30;
    
    public static Pair<CostHolder, List<String>> ummmList(List<String> possibleCosts) {
        List<Item> itemCosts = new ArrayList<>();
        List<String> illegalStrings = new ArrayList<>();
        List<TagKey<Item>> tagCosts = new ArrayList<>();

        possibleCosts.forEach(resource -> {
            if (ResourceUtil.isTag(resource)) {
                tagCosts.add(ResourceUtil.parseTag(resource));
            } else {
                var rl = ResourceLocation.tryParse(resource);
                if (rl == null) {
                    illegalStrings.add(resource);
                    return;
                }
                ResourceUtil.getOptionalItem(rl)
                        .ifPresentOrElse(itemCosts::add, () -> illegalStrings.add(resource));
            }
        });

        return Pair.of(makeCost(itemCosts, tagCosts), illegalStrings); 
    }


    // parses json. Takes in path to create a more helpful error message when there are invalid items
    public static CostHolder parseJsonAndError(JsonElement element, String enchantment, ResourceLocation path) {
        List<String> possibleCosts = JsonUtil.listOrSingleton(element);

        List<Item> itemCosts = new ArrayList<>();
        List<String> illegalStrings = new ArrayList<>();
        List<TagKey<Item>> tagCosts = new ArrayList<>();

        possibleCosts.forEach(resource -> {
            if (ResourceUtil.isTag(resource)) {
                tagCosts.add(ResourceUtil.parseTag(resource));
            } else {
                var rl = ResourceLocation.tryParse(resource);
                if (rl == null) {
                    illegalStrings.add(resource);
                    return;
                }
                ResourceUtil.getOptionalItem(rl)
                        .ifPresentOrElse(itemCosts::add, () -> illegalStrings.add(resource));
            }
        });

        if (error(illegalStrings, enchantment, path)) {
            return null;
        }

        return makeCost(itemCosts, tagCosts);
    }

    public static boolean test(CostHolder costHolder, ItemStack stack) {
        return anyMatch(costHolder, stack::is, stack::is);
    }

    public static CostHolder makeCost(List<Item> itemCosts, List<TagKey<Item>> tagCosts) {
        return new CostHolder(Util.make(new ArrayList<>(), list -> {
            CSE20Util.tryUnwrapSingleton(itemCosts)
                    .ifPresentOrElse(item -> list.add(singleItem(item, CONJURE_XP_COST)),
                            () -> list.add(multipleItems(itemCosts, CONJURE_XP_COST)));
            tagCosts.forEach(tag -> list.add(tag(tag, CONJURE_XP_COST)));
        }));
    }

    public static boolean error(List<String> illegalItems, String enchantment, ResourceLocation filePath) {
        if (!illegalItems.isEmpty()) {
            DoltasticEnchantments.LOGGER.warn("Tried to associate invalid item {} to enchantment {} in {}, aborting", EnchantCostUtil.reduceToString(illegalItems, Function.identity(), ", "), enchantment, filePath);
            return true;
        }
        return false;
    }

    public static boolean hasModdedIds(CostHolder data) {
        return anyMatch(data, item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft"), tag -> tag.location().getNamespace().equals("minecraft"));
    }

    public static boolean isEmpty(CostHolder data) {
        for (Cost cost : data.costs()) {
            ItemCostIngredient ing = cost.itemCost().ingredient();
            if (ing instanceof ItemCostIngredient.SingleItem(Item item) && item != null) return false;
            if (ing instanceof ItemCostIngredient.ItemList(List<Item> list) && !list.isEmpty()) return false;
            if (ing instanceof ItemCostIngredient.TagIngredient(TagKey<Item> tag) && BuiltInRegistries.ITEM.getTag(tag).map(named -> named.size() > 0).orElse(false)) return false;
        }
        return true;
    }

    public static boolean allMatch(CostHolder data, Predicate<Item> itemPred, Predicate<TagKey<Item>> tagPred) {
        return anyMatch(data, Predicate.not(itemPred), Predicate.not(tagPred));
    }

    public static boolean anyMatch(CostHolder data, Predicate<Item> itemPred, Predicate<TagKey<Item>> tagPred) {
        return data.costs().stream().anyMatch(cost -> {
            ItemCostIngredient itemCostIngredient = cost.itemCost().ingredient();

            return switch (itemCostIngredient) {
                case ItemCostIngredient.TagIngredient(TagKey<Item> tag) -> tagPred.test(tag);
                case ItemCostIngredient.ItemList(List<Item> list) -> list.stream().anyMatch(itemPred);
                case ItemCostIngredient.SingleItem(Item item) -> itemPred.test(item);
                default ->
                        throw new IllegalStateException("Found instance of ItemCostIngredient that's not any of the three possible subclasses");
            };
        });
    }

    public static <T> List<T> map(CostHolder data, Function<? super Item, T> itemOps, Function<? super TagKey<Item>, T> tagOps) {
        List<T> list = new ArrayList<>();
        data.costs().forEach(cost -> {
            ItemCostIngredient itemCostIngredient = cost.itemCost().ingredient();

            switch (itemCostIngredient) {
                case ItemCostIngredient.TagIngredient(TagKey<Item> tag) -> list.add(tagOps.apply(tag));
                case ItemCostIngredient.ItemList(List<Item> itemList) ->
                        itemList.stream().map(itemOps).forEach(list::add);
                case ItemCostIngredient.SingleItem(Item item) -> list.add(itemOps.apply(item));
                default ->
                        throw new IllegalStateException("Found instance of ItemCostIngredient that's not any of the three possible subclasses");
            }
        });
        return list;
    }

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

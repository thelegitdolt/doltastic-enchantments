package com.dolthhaven.doltasticenchantments.core.utils;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.google.gson.JsonElement;
import me.alfie.alfinolib.util.codec.ItemCostIngredient;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CostHolderUtils {
    public static final int CONJURE_XP_COST = 30;

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

    private static CostHolder makeCost(List<Item> itemCosts, List<TagKey<Item>> tagCosts) {
        return new CostHolder(Util.make(new ArrayList<>(), list -> {
            CSE20Util.tryUnwrapSingleton(itemCosts)
                    .ifPresentOrElse(item -> list.add(EnchantCostUtil.singleItem(item, CONJURE_XP_COST)),
                            () -> list.add(EnchantCostUtil.multipleItems(itemCosts, CONJURE_XP_COST)));
            tagCosts.forEach(tag -> list.add(EnchantCostUtil.tag(tag, CONJURE_XP_COST)));
        }));
    }

    public static boolean error(List<String> illegalItems, String enchantment, ResourceLocation filePath) {
        if (!illegalItems.isEmpty()) {
            DoltasticEnchantments.LOGGER.warn("Tried to associate invalid item {} to enchantment {} in {}, aborting", EnchantCostUtil.reduceToString(illegalItems, Function.identity(), ", "), enchantment, filePath);
            return false;
        }
        return true;
    }

    public static boolean hasModdedIds(CostHolder data) {
        return anyMatch(data, item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft"), tag -> tag.location().getNamespace().equals("minecraft"));
    }

    public static boolean allMatch(CostHolder data, Predicate<Item> itemPred, Predicate<TagKey<Item>> tagPred) {
        return anyMatch(data, Predicate.not(itemPred), Predicate.not(tagPred));
    }

    public static boolean anyMatch(CostHolder data, Predicate<Item> itemPred, Predicate<TagKey<Item>> tagPred) {
        return data.costs().stream().anyMatch(cost -> {
            ItemCostIngredient itemCostIngredient = cost.itemCost().ingredient();

            if (itemCostIngredient instanceof ItemCostIngredient.TagIngredient(TagKey<Item> tag)) return tagPred.test(tag);
            else if (itemCostIngredient instanceof ItemCostIngredient.ItemList(List<Item> list))
                return list.stream().anyMatch(itemPred);
            else if (itemCostIngredient instanceof ItemCostIngredient.SingleItem(Item item))
                return itemPred.test(item);

            throw new IllegalStateException("Found instance of ItemCostIngredient that's not any of the three possible subclasses");
        });
    }

    public static <T> List<T> map(CostHolder data, Function<? super Item, T> itemOps, Function<? super TagKey<Item>, T> tagOps) {
        List<T> list = new ArrayList<>();
        data.costs().forEach(cost -> {
            ItemCostIngredient itemCostIngredient = cost.itemCost().ingredient();

            if (itemCostIngredient instanceof ItemCostIngredient.TagIngredient(TagKey<Item> tag)) list.add(tagOps.apply(tag));
            else if (itemCostIngredient instanceof ItemCostIngredient.ItemList(List<Item> itemList))
                itemList.stream().map(itemOps).forEach(list::add);
            else if (itemCostIngredient instanceof ItemCostIngredient.SingleItem(Item item))
                list.add(itemOps.apply(item));

            throw new IllegalStateException("Found instance of ItemCostIngredient that's not any of the three possible subclasses");
        });
        return list;
    }
}

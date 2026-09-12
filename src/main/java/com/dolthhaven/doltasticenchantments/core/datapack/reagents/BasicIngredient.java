package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.utils.CSE20Util;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.JsonUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.google.gson.JsonElement;
import me.alfie.alfinolib.util.codec.ItemCostIngredient;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

// item predicate class for reagents that may contain a tag or a list of items
public record BasicIngredient(CostHolder cost) {
    public static final int CONJURE_XP_COST = 30;

    // parses json. Takes in path to create a more helpful error message when there are invalid items
    public static CostHolder parseJsonAndError(JsonElement element, ResourceLocation path) {
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

        if (error(illegalStrings, path)) {
            return null;
        }

        return makeCost(itemCosts, tagCosts);
    }

    private static CostHolder makeCost(List<Item> itemCosts, List<TagKey<Item>> tagCosts) {
        return new CostHolder(Util.make(new ArrayList<>(), list -> {
                CSE20Util.tryUnwrapSingleton(itemCosts)
                        .ifPresentOrElse(item -> list.add(EnchantCostUtil.singleItem(item, CONJURE_XP_COST)),
                                () -> list.add(EnchantCostUtil.multipleItems(itemCosts, CONJURE_XP_COST)));
                tagCosts.forEach(tag -> list.add(EnchantCostUtil.tag(tag, CONJURE_XP_COST)));
            }));
    }

    public boolean test(ItemStack stack) {
        if (isItems() && cost == EMPTY_COST_GROUP) return true;
        if (isTag()) {
            return stack.is(tag);
        } else {
            for (CostDefinition definition : cost.children()) {
                if (definition instanceof CostEntry entry) {
                    if (stack.is(entry.asItem())) {
                        return true;
                    }
                } else throw new IllegalStateException("BasicIngredients.cost can only be nested one layer");
            }
        } return false;
    }

    public String encodeAsString() {
        if (isItems()) {
            return "[" + EnchantCostUtil.reduceToString(this.castedCost(), CostEntry::item, ",") +  "]";
        } else {
            return "#" + tag.location();
        }
    }

    public static BasicIngredient decode(String bytes) {
        if (bytes.startsWith("#")) {
            return new BasicIngredient(EMPTY_COST_GROUP, TagKey.create(Registries.ITEM, new ResourceLocation(bytes.substring(1))));
        } else {
            return new BasicIngredient(new CostGroup(Arrays.stream(bytes.substring(1, bytes.length() - 1).split(",")).map(thing ->
                    (CostDefinition) EnchantCostUtil.basicCost(thing, CONJURE_XP_COST)).toList(), GroupType.ANY_OF), null);
        }
    }

    public static boolean error(List<String> illegalItems, ResourceLocation filePath) {
        if (!illegalItems.isEmpty()) {
            DoltasticEnchantments.LOGGER.warn("Tried to associate invalid items {} to enchantment {} in {}, aborting", EnchantCostUtil.reduceToString(illegalItems, Function.identity(), ", "), enchantment, filePath);
            return false;
        }
        return true;
    }

    public static boolean hasModdedIds(CostHolder data) {
        return data.costs().stream().anyMatch(cost -> {
            ItemCostIngredient itemCostIngredient = cost.itemCost().ingredient();
            if (itemCostIngredient instanceof ItemCostIngredient.TagIngredient(TagKey<Item> tag)) return tag.location().getNamespace().equals("minecraft");
            else if (itemCostIngredient instanceof ItemCostIngredient.ItemList(List<Item> list))
                return list.stream().anyMatch(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft"));
            else if (itemCostIngredient instanceof ItemCostIngredient.SingleItem(Item item))
                return BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft");

            throw new IllegalStateException("Found instance of ItemCostIngredient that's not any of the three possible subclasses");
        });
    }

    public boolean isTag() {
        return tag != null;
    }

    public boolean isItems() {
        return tag == null;
    }

    public List<CostEntry> castedCost() {
        try {
            return cost.children().stream().map(CostEntry.class::cast).toList();
        } catch (ClassCastException exception) {
            throw new IllegalStateException("The children of BasicIngredients.cost must be Cost Entries, no nesting is permitted");
        }
    }
}

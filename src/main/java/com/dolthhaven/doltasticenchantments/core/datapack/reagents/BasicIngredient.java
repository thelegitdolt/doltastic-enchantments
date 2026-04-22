package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import com.google.gson.JsonElement;
import me.alfie.immersiveenchanting.datapack.cost.CostDefinition;
import me.alfie.immersiveenchanting.datapack.cost.CostEntry;
import me.alfie.immersiveenchanting.datapack.cost.CostGroup;
import me.alfie.immersiveenchanting.datapack.cost.GroupType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("removal")
public record BasicIngredient(CostGroup cost, TagKey<Item> tag) {
    public static final CostGroup EMPTY_COST_GROUP = new CostGroup(List.of(), GroupType.ANY_OF);
    public static final int ENCHANT_COST = 20;
    public static final BasicIngredient EMPTY = new BasicIngredient(EMPTY_COST_GROUP, null);

    public BasicIngredient(List<CostEntry> costEntries) {
        this(new CostGroup(costEntries.stream().map(CostDefinition.class::cast).toList(), GroupType.ANY_OF), null);
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

    public static BasicIngredient parseJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            List<CostDefinition> items = jsonElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString)
                    .map(name -> (CostDefinition) new CostEntry(name, "", 1, ENCHANT_COST)).toList();
            return new BasicIngredient(new CostGroup(items, GroupType.ANY_OF), null);
        } else {
            String string = jsonElement.getAsString();
            if (string.startsWith("#")) {
                return new BasicIngredient(EMPTY_COST_GROUP, TagKey.create(Registries.ITEM, new ResourceLocation(string.substring(1))));
            } else {
                return new BasicIngredient(new CostGroup(List.of(EnchantCostUtil.basicCost(string, 20)), GroupType.ANY_OF), null);
            }
        }
    }

    public static BasicIngredient decode(String bytes) {
        if (bytes.startsWith("#")) {
            return new BasicIngredient(EMPTY_COST_GROUP, TagKey.create(Registries.ITEM, new ResourceLocation(bytes.substring(1))));
        } else {
            return new BasicIngredient(new CostGroup(Arrays.stream(bytes.substring(1, bytes.length() - 1).split(",")).map(thing ->
                    (CostDefinition) EnchantCostUtil.basicCost(thing, ENCHANT_COST)).toList(), GroupType.ANY_OF), null);
        }
    }

    public boolean isValid(Registry<Item> registry, ResourceLocation enchantment, ResourceLocation filePath) {
        if (!isTag()){
            List<String> illegalItems = new ArrayList<>();
            this.castedCost().forEach(entry -> {
                if (!registry.containsKey(ResourceKeyUtil.sitem(entry.item()))) {
                    illegalItems.add(entry.item());
                }
            });
            if (!illegalItems.isEmpty()) {
                DoltasticEnchantments.LOGGER.info("Tried to associate invalid items {} to enchantment {} in {}, aborting", EnchantCostUtil.reduceToString(illegalItems, Function.identity(), ", "), enchantment, filePath);
                return false;
            }
        } return true;
    }

    public boolean hasModdedIds() {
        if (isTag()) return !tag.location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE);
        return this.castedCost().stream().anyMatch(key -> !new ResourceLocation(key.item()).getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE));
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

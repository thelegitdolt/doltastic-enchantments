package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import com.google.gson.JsonElement;
import me.alfie.immersiveenchanting.datapack.cost.*;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("removal")
public record BasicIngredient(List<ResourceKey<Item>> itemKeys, TagKey<Item> tag) {
    public static final BasicIngredient EMPTY = new BasicIngredient(new ArrayList<>(), null);

    public boolean test(ItemStack stack, Registry<Item> itemReg) {
        if (isItems() && itemKeys.isEmpty()) return true;
        if (isTag()) {
            return stack.is(tag);
        } else {
            for (ResourceKey<Item> key : itemKeys) {
                if (stack.is(itemReg.get(key))) {
                    return true;
                }
            }
        } return false;
    }

    public boolean test(ItemStack stack, RegistryAccess access) {
        return test(stack, access.registryOrThrow(Registries.ITEM));
    }

    public String encodeAsString() {
        if (isItems()) {
            return "[" + EnchantCostUtil.keyListToString(itemKeys, ",") +  "]";
        } else {
            return "#" + tag.location();
        }
    }

    public static BasicIngredient parseJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            List<ResourceKey<Item>> items = jsonElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString).map(ResourceKeyUtil::sitem).toList();
            return new BasicIngredient(items, null);
        } else {
            String string = jsonElement.getAsString();
            if (string.startsWith("#")) {
                return new BasicIngredient(new ArrayList<>(), TagKey.create(Registries.ITEM, new ResourceLocation(string.substring(1))));
            } else {
                return new BasicIngredient(List.of(ResourceKeyUtil.sitem(string)), null);
            }
        }
    }

    public static BasicIngredient decode(String bytes) {
        if (bytes.startsWith("#")) {
            return new BasicIngredient(new ArrayList<>(), TagKey.create(Registries.ITEM, new ResourceLocation(bytes.substring(1))));
        } else {
            return new BasicIngredient(Arrays.stream(bytes.substring(1, bytes.length() - 1).split(",")).map(thing ->
                    ResourceKey.create(Registries.ITEM, new ResourceLocation(thing))).toList(), null);
        }
    }

    public boolean isValid(Registry<Item> registry, ResourceLocation enchantment, ResourceLocation filePath) {
        if (!isTag()){
            List<ResourceKey<Item>> illegalItems = new ArrayList<>();
            itemKeys.forEach(itemKey -> {
                if (!registry.containsKey(itemKey)) {
                    illegalItems.add(itemKey);
                }
            });
            if (!illegalItems.isEmpty()) {
                DoltasticEnchantments.LOGGER.info("Tried to associate invalid items {} to enchantment {} in {}, aborting", EnchantCostUtil.keyListToString(illegalItems, ", "), enchantment, filePath);
                return false;
            }
        } return true;
    }

    public boolean hasModdedIds() {
        if (isTag()) return !tag.location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE);
        return itemKeys.stream().anyMatch(key -> !key.location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE));
    }

    public boolean isTag() {
        return tag != null;
    }

    public boolean isItems() {
        return tag == null;
    }
}

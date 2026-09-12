package com.dolthhaven.doltasticenchantments.core.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

@SuppressWarnings("removal")
public class ResourceUtil {
    public static ResourceKey<Item> item(ResourceLocation location) {
        return ResourceKey.create(Registries.ITEM, location);
    }

    public static ResourceKey<Enchantment> enchant(ResourceLocation location) {
        return ResourceKey.create(Registries.ENCHANTMENT, location);
    }

    public static boolean isTag(String str) {
       return str.startsWith("#");
    }

    public static TagKey<Item> parseTag(String str) {
        if (!isTag(str)) {
            throw new IllegalArgumentException("Not a real tag doesn't start with #");
        }
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(str.substring(1)));
    }

    public static ResourceKey<Item> sitem(String location) {
        return item(ResourceLocation.parse(location));
    }

    public static ResourceKey<Enchantment> senchant(String location) {
        return enchant(ResourceLocation.parse(location));
    }

    public static boolean isTag(Holder<Enchantment> holder, TagKey<Enchantment> tag, Registry<Enchantment> enchantReg) {
        return enchantReg.getTag(tag).map(key -> key.contains(holder)).orElse(false);
    }

}

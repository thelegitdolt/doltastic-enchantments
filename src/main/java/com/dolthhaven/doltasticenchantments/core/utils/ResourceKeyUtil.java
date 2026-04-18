package com.dolthhaven.doltasticenchantments.core.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class ResourceKeyUtil {
    public static ResourceKey<Item> item(ResourceLocation location) {
        return ResourceKey.create(Registries.ITEM, location);
    }

    public static ResourceKey<Enchantment> enchant(ResourceLocation location) {
        return ResourceKey.create(Registries.ENCHANTMENT, location);
    }
}

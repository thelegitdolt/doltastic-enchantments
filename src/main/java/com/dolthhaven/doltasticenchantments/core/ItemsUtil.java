package com.dolthhaven.doltasticenchantments.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class ItemsUtil {
    public static List<ResourceKey<Enchantment>> getAllStoredEnchantments(ItemStack bookStack) {
        List<ResourceKey<Enchantment>> enchants = new ArrayList<>();

        ListTag listTag = EnchantedBookItem.getEnchantments(bookStack);
        if (!listTag.isEmpty()) {
            for (Tag tag : listTag) {
                if (tag instanceof CompoundTag compoundTag) {
                    ResourceLocation enchantmentRL = ResourceLocation.tryParse(compoundTag.getString("id"));
                    if (enchantmentRL == null) continue;
                    enchants.add(ResourceKey.create(Registries.ENCHANTMENT, enchantmentRL));
                }
            }
        }
        return enchants;
    }
}

package com.dolthhaven.doltasticenchantments.core;

import me.alfie.immersiveenchanting.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public class BookUtil {
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

    public static ItemStack newBookWith(List<Holder<Enchantment>> enchants) {
        ItemStack stack = new ItemStack(ModItems.ANCIENT_BOOK.get());
        ListTag listtag = new ListTag();
        for (Holder<Enchantment> holder : enchants) {
            ResourceLocation resourcelocation = holder.unwrapKey().orElseThrow().location();
            listtag.add(EnchantmentHelper.storeEnchantment(resourcelocation, 1));
        }
        stack.getOrCreateTag().put("StoredEnchantments", listtag);
        return stack;
    }
}

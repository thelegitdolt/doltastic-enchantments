package com.dolthhaven.doltasticenchantments.core.utils;

import me.alfie.immersiveenchanting.item.AncientBook;
import me.alfie.immersiveenchanting.item.ModItems;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class BookUtil {
//    public static List<ResourceKey<Enchantment>> getAllStoredEnchantments(ItemStack bookStack) {
//        List<ResourceKey<Enchantment>> enchants = new ArrayList<>();
//
//        ListTag listTag = EnchantedBookItem.getEnchantments(bookStack);
//        if (!listTag.isEmpty()) {
//            for (Tag tag : listTag) {
//                if (tag instanceof CompoundTag compoundTag) {
//                    ResourceLocation enchantmentRL = ResourceLocation.tryParse(compoundTag.getString("id"));
//                    if (enchantmentRL == null) continue;
//                    enchants.add(ResourceKey.create(Registries.ENCHANTMENT, enchantmentRL));
//                }
//            }
//        }
//        return enchants;
//    }

    public static ItemStack newBookWith(List<Holder<Enchantment>> enchants) {
        ItemStack stack = new ItemStack(ModItems.ANCIENT_BOOK.get());
        ItemEnchantments.Mutable itemEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.forEach(enchant -> itemEnchantments
                .set(enchant, 1));

        stack.set(DataComponents.STORED_ENCHANTMENTS, itemEnchantments.toImmutable());
        return stack;
    }

    public static ItemStack newBookWith(Holder<Enchantment> enchant) {
        ItemStack stack = new ItemStack(ModItems.ANCIENT_BOOK.get());
        EnchantmentUtil.setStoredEnchantment(stack, enchant);
        return stack;
    }

    public static void drop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack,  true);
        }
    }
}

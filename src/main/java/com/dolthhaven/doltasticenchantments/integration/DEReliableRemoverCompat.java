package com.dolthhaven.doltasticenchantments.integration;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.lang.reflect.Method;

public class DEReliableRemoverCompat {
    public static boolean isEnchantmentRemoved(Holder<Enchantment> enchantment) {
        try {
            Class<?> rrApi = Class.forName("com.evandev.reliable_remover.api.ReliableRemoverAPI");
            Method isEnchantBlocked = rrApi.getMethod("isEnchantmentBlocked", ItemStack.class, Holder.class);
            return (boolean) isEnchantBlocked.invoke(enchantment);
        }  catch (ReflectiveOperationException e) {
            return true;
        }
    }
}

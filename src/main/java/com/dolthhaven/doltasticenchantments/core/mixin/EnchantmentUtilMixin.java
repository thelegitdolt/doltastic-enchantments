package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.integration.DEReliableRemoverCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnchantmentUtil.class)
public class EnchantmentUtilMixin {
    @ModifyReturnValue(method = "getAllEnchantmentsInRegistry", at = @At("RETURN"))
    private static List<Holder<Enchantment>> DoltasticEnchantments$ReliablyRemovesEnchantments(List<Holder<Enchantment>> original) {
        return original.stream().filter(enchant -> !DEReliableRemoverCompat.isEnchantmentRemoved(enchant)).toList();
    }
}

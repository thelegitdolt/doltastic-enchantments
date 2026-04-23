package com.dolthhaven.doltasticenchantments.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alfie.immersiveenchanting.util.CostHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins CostHelper so it supports an empty cost
 */
@Mixin(CostHelper.class)
public abstract class CostHelperMixin {
    @WrapOperation(method = "findValidCost", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean DoltasticEnchantments$AirIsAnyItem(ItemStack instance, Item item, Operation<Boolean> original) {
        if (item == Items.AIR) return true;
        else return original.call(instance, item);
    }
}

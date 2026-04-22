package com.dolthhaven.doltasticenchantments.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantingTableMenu.class)
public abstract class EnchantingTableMenuMixin {
    @WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEnchantable(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean DoltasticEnchantments$BooksGoToToolSlot(Item instance, ItemStack pStack, Operation<Boolean> original) {
        return pStack.is(Items.BOOK) || original.call(instance, pStack);
    }
}

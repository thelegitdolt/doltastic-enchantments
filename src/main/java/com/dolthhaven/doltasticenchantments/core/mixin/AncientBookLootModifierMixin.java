package com.dolthhaven.doltasticenchantments.core.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.alfie.immersiveenchanting.loot.InjectAncientBookLootModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InjectAncientBookLootModifier.class)
public class AncientBookLootModifierMixin {
    @Inject(method = "doApply", at = @At("HEAD"), cancellable = true, remap = false)
    private void DoltasticEnchantments$MyLootModifierIsBetter(ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        cir.setReturnValue(generatedLoot);
    }
}

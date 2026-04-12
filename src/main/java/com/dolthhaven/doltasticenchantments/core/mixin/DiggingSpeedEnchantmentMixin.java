package com.dolthhaven.doltasticenchantments.core.mixin;

import net.minecraft.world.item.enchantment.DiggingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggingEnchantment.class)
public class DiggingSpeedEnchantmentMixin {
    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void DoltasticEnchantments$SetEfficiencyMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(3);
    }
}

package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.integration.emi.DEReliableRemoverCompat;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
import me.alfie.immersiveenchanting.events.creative.CreativeEvents;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeEvents.class)
public class CreativeEventsMixin {
    @Inject(method = "lambda$onBuildModCreativeTab$0", at = @At("HEAD"), cancellable = true)
    private static void DoltasticEnchantments$HideReliablyRemovedBooks(BuildCreativeModeTabContentsEvent event, Holder.Reference<Enchantment> holder, CallbackInfo ci) {
        if (DoltasticEnchantments.reliableRemover() && DEReliableRemoverCompat.isEnchantmentRemoved(holder.value())) ci.cancel();
        if (!EnchantCostUtil.requiresBook(EnchantmentCostRegistry.getClientRegistry().getEnchantmentCost(holder.unwrapKey().orElseThrow()))) ci.cancel();
    }
}

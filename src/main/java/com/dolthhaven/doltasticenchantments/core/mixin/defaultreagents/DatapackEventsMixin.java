package com.dolthhaven.doltasticenchantments.core.mixin.defaultreagents;

import com.dolthhaven.doltasticenchantments.core.datapack.RegistryAccessHolder;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostDatapack;
import me.alfie.immersiveenchanting.events.datapack.DatapackEvents;
import net.minecraftforge.event.AddReloadListenerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DatapackEvents.class)
public class DatapackEventsMixin {
    @Inject(method = "addInModDatapack", at = @At("HEAD"), remap = false)
    private static void DoltasticEnchantments$SetRegistryAccess(AddReloadListenerEvent event, CallbackInfo ci) {
        if (EnchantmentCostDatapack.DATAPACK instanceof RegistryAccessHolder holder) {
            holder.setRegistryAccess(event.getRegistryAccess());
        }
    }
}

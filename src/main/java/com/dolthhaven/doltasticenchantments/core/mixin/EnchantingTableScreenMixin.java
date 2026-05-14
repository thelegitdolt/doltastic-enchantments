package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.datapack.ReagentStackHolder;
import com.dolthhaven.doltasticenchantments.core.utils.ReagentStackUtil;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.tab.enchanting.EnchantingTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableScreen.class)
public class EnchantingTableScreenMixin {
    @Shadow(remap = false) @Final public EnchantingTab enchantingTab;

    // adds a listener for reagent slots, for ancient books
    @Inject(method = "containerTick", at = @At("TAIL"))
    private void DoltasticEnchants$UpdateReagentStack(CallbackInfo ci) {
        ReagentStackUtil.checkReagentSlotUpdated((EnchantingTableScreen) (Object) this, (ReagentStackHolder) this.enchantingTab);
    }
}

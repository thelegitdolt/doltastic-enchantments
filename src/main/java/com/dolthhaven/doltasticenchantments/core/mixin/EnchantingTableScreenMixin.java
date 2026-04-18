package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting.ReagentStackHolder;
import com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting.ReagentStackUtil;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.EnchantingTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableScreen.class)
public class EnchantingTableScreenMixin {
    @Shadow @Final public EnchantingTab enchantingTab;

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void DoltasticEnchants$UpdateReagentStack(CallbackInfo ci) {
        ReagentStackUtil.checkReagentSlotUpdated((EnchantingTableScreen) (Object) this, (ReagentStackHolder) this.enchantingTab);
    }
}

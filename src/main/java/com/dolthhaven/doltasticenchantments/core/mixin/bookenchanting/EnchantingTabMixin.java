package com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting;

import com.dolthhaven.doltasticenchantments.core.datapack.ReagentStackHolder;
import com.dolthhaven.doltasticenchantments.core.utils.ReagentStackUtil;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.EnchantingTab;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Mixin(EnchantingTab.class)
public class EnchantingTabMixin implements ReagentStackHolder {
    @Shadow @Final public EnchantingTableScreen screen;
    @Unique private ItemStack reagentStack;

    @Override
    public ItemStack lastReagentStack() {
        return reagentStack;
    }

    @Override
    public void setReagentStack(ItemStack stack) {
        reagentStack = stack;
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void populateField(EnchantingTableScreen screen, CallbackInfo ci) {
        reagentStack = ItemStack.EMPTY;
    }

    @Inject(method = "onToolSlotUpdate", at = @At("HEAD"), cancellable = true, remap = false)
    private void DoltasticEnchantments$updateBookEnchanting(CallbackInfo ci) {
        if (ReagentStackUtil.bookEnchantingUpdate(this.screen, (EnchantingTab) (Object) this, this.screen.getMenu().getCostSlotItem())) ci.cancel();
    }
}

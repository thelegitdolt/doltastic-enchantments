package com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting;

import me.alfie.immersiveenchanting.gui.core.tab.enchanting.EnchantingTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EnchantingTab.class)
public class EnchantingTabMixin implements ReagentStackHolder{
    @Unique private ItemStack reagentStack = ItemStack.EMPTY;

    @Override
    public ItemStack lastReagentStack() {
        return reagentStack;
    }

    @Override
    public void setReagentStack(ItemStack stack) {
        reagentStack = stack;
    }
}

package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.common.enchanting.ReagentStackHolder;
import com.dolthhaven.doltasticenchantments.core.utils.ReagentStackUtil;
import me.alfie.alfinolib.gui.CommonAbstractContainerScreen;
import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableScreen.class)
public abstract class EnchantingTableScreenMixin extends CommonAbstractContainerScreen<EnchantingTableMenu> implements ReagentStackHolder {
    @Unique
    private ItemStack lastReagentStack;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sex(EnchantingTableMenu menu, Inventory inventory, Component title, CallbackInfo ci) {
        this.lastReagentStack = ItemStack.EMPTY;
    }

    // adds a listener for reagent slots, for ancient books
    @Inject(method = "containerTick", at = @At("TAIL"))
    private void DoltasticEnchants$UpdateReagentStack(CallbackInfo ci) {
        ItemStack stack = this.getMenu().getSlot(EnchantingTableMenu.Slots.COST.id()).getItem();
        ReagentStackUtil.checkReagentSlotUpdated((EnchantingTableScreen) (Object) this, this, stack);
    }

    @Override
    public ItemStack lastReagentStack() {
        return lastReagentStack;
    }

    @Override
    public void setReagentStack(ItemStack stack) {
        lastReagentStack = stack;
    }

    public EnchantingTableScreenMixin(EnchantingTableMenu menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }
}

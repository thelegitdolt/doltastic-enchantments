package com.dolthhaven.doltasticenchantments.core.mixin.bookenchanting;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.AncientBookNode;
import com.dolthhaven.doltasticenchantments.core.datapack.ReagentStackHolder;
import com.dolthhaven.doltasticenchantments.core.utils.ReagentStackUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.EnchantingTab;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.Node;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeTooltip;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTab.class)
public class EnchantingTabMixin implements ReagentStackHolder {
    @Shadow @Final public EnchantingTableScreen screen;
    @Shadow private NodeTooltip nodeTooltip;

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

    @WrapOperation(method = "renderNodeTooltip", at = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/util/FxHelper;playNodeHoverSound(Lme/alfie/immersiveenchanting/gui/core/tab/enchanting/node/Node;Lnet/minecraft/world/entity/player/Player;)V"), remap = false)
    private void DoltasticEnchantments$ProcessModNodes(Node pitch, Player highestLevel, Operation<Void> original, @Local(argsOnly = true) Node node) {
        if (node instanceof AncientBookNode ancientNode) {
            nodeTooltip = new AncientBookNode.AncientNodeTooltips(ancientNode, this.screen);
        }
        original.call(pitch, highestLevel);
    }
}

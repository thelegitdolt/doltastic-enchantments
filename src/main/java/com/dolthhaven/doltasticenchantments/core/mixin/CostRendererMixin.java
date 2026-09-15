package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.ConjureNodeData;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import me.alfie.immersiveenchanting.gui.tab.enchanting.tooltip.CostRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CostRenderer.class)
public class CostRendererMixin {
    @Shadow private CostHolder holder;

    @Inject(method = "setCostToRender", at = @At("TAIL"))
    private void DoltasticEnchantments$findConjuringCost(ResourceId id, int level, CallbackInfo ci) {
        if (this.holder == CostHolder.EMPTY && id.path().endsWith(ConjureNodeData.FLAG)) {
            ReagentsRegistry registry = ReagentsRegistry.client();
            CostHolder costHolder = registry.findWithoutRegAccess(ConjureNodeData.unwrapFlag(id).mc(), holder -> holder.unwrapKey().orElseThrow().location());
            if (costHolder != null) {
                this.holder = costHolder;
            }
        }
    }
}

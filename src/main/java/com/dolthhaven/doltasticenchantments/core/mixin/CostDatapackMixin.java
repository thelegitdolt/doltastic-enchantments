package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.integration.DEReliableRemoverCompat;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import me.alfie.alfinolib.datapacks.DatapackDefinition;
import me.alfie.alfinolib.datapacks.ModDatapack;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.CostDatapack;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.CostRegistry;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CostDatapack.class)
public abstract class CostDatapackMixin extends ModDatapack<CostDatapack, CostRegistry> {
    @Shadow private CostRegistry DATA;

    public CostDatapackMixin(Codec<CostDatapack> codec, DatapackDefinition<CostRegistry> definition, RegistryAccess registryAccess) {
        super(codec, definition, registryAccess);
    }

    // gives every single enchantment without a cost defined the default fairy dust cost
    // all costs added by immersive enchanting are filtered out in the pack.mcmeta file
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void DoltasticEnchantments$PopulateDefaultCosts(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller, CallbackInfo ci) {
        EnchantmentUtil.getAllEnchantmentsInRegistry(this.getRegistryLookup()).forEach(enchantment -> {
            ResourceId enchantId = ResourceId.parse(enchantment.getRegisteredName());
            if (!this.DATA.isRegistered(enchantId))
                this.DATA.register(enchantId, EnchantCostUtil.defaultCost(enchantment.value().getMaxLevel()));
        });
        EnchantCostUtil.DEFAULT_COUNT = null;
    }
}

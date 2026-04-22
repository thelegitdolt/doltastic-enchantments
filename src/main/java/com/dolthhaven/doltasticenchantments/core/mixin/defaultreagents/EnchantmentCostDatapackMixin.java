package com.dolthhaven.doltasticenchantments.core.mixin.defaultreagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.RegistryAccessHolder;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostDatapack;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import me.alfie.immersiveenchanting.datapack.parser.EnchantmentCostParseException;
import me.alfie.immersiveenchanting.datapack.parser.JsonProperty;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("removal")
@Mixin(EnchantmentCostDatapack.class)
public class EnchantmentCostDatapackMixin implements RegistryAccessHolder {
    @Unique private static final String DEFAULT_FAIRY_DUST_AMOUNT = "fairyDustAmounts";
    @Unique private static final String MAX_LEVEL = "maxLevel";

    @Unique
    private RegistryAccess access;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At("HEAD"), remap = false)
    private void DoltasticEnchants$RemoveInvalidEnchants(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, @Share("enchant") LocalRef<Registry<Enchantment>> enchant) {
        Registry<Enchantment> enchantReg = access.registry(Registries.ENCHANTMENT).orElseThrow(() -> new EnchantmentCostParseException("Cannot unpack enchantment costs, registry not found"));
        enchant.set(enchantReg);
        Set<ResourceLocation> removedEnchants = new HashSet<>();
        object.keySet().removeIf(location -> {
            ResourceLocation enchantLoc = new ResourceLocation(location.getPath().replace('/', ':'));
            for (var cost : EnchantmentCostRegistry.InternalCosts.values()) {
                if (cost.getId().equals(enchantLoc.toString())) return false;
            }

            if (!enchantReg.containsKey(enchantLoc)) {
                removedEnchants.add(enchantLoc);
                return true;
            } return false;
        });
        if (!removedEnchants.isEmpty()){
            DoltasticEnchantments.LOGGER.error("DOLTASTIC ENCHANT: The following paths in Enchantment Cost datapack are invalid, since they do not correspond to a real enchantment: {}",
                    EnchantCostUtil.reduceToString(removedEnchants, Function.identity(), ", "));
        }

    }

    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/datapack/parser/DatapackParser;parseJson(Lcom/google/gson/JsonElement;)Lme/alfie/immersiveenchanting/datapack/cost/EnchantmentCost;"), remap = false)
    private EnchantmentCost DoltasticEnchantments$AddDefaultEnchantmentCost(JsonElement levelKey, Operation<EnchantmentCost> original, @Local String[] parts, @Share("enchant") LocalRef<Registry<Enchantment>> enchant) {
        try {
            return original.call(levelKey);
        } catch (EnchantmentCostParseException exception) {
            ResourceLocation enchantLoc = new ResourceLocation(parts[0], parts[1]);
            Enchantment enchantment = requireNonNull(enchant.get().get(enchantLoc));
            boolean enabled = true;
            int maxLevel = enchantment.getMaxLevel();
            JsonObject root = levelKey.getAsJsonObject();
            if (root.has(JsonProperty.ENABLED.getKey())) {
                enabled = root.get(JsonProperty.ENABLED.getKey()).getAsBoolean();
            }
            if (root.has(MAX_LEVEL)) {
                maxLevel = root.get(MAX_LEVEL).getAsInt();
            }

            List<Integer> dustCosts;
            if (root.has(DEFAULT_FAIRY_DUST_AMOUNT)) {
                JsonElement fairyDustAmounts = root.get(DEFAULT_FAIRY_DUST_AMOUNT);
                var rawAmounts = fairyDustAmounts.getAsJsonArray().asList();
                if (rawAmounts.size() > maxLevel) {
                    throw new EnchantmentCostParseException("Found %d entries in fairyDustAmount beyond %d expected for enchantment %s"
                            .formatted(rawAmounts.size(), maxLevel, enchantLoc));
                }
                dustCosts = rawAmounts.stream().map(JsonElement::getAsInt).toList();
            } else {
                dustCosts = EnchantCostUtil.defaultCosts(maxLevel);
            }
            return EnchantCostUtil.createFairyDustCosts(enabled, dustCosts);
        }
    }

    @Override
    public void setRegistryAccess(RegistryAccess access) {
        this.access = access;
    }
}

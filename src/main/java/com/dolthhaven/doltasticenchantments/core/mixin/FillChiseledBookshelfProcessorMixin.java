package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.dolthhaven.doltasticenchantments.integration.emi.DEReliableRemoverCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alfie.immersiveenchanting.structure.FillChiseledBookshelfProcessor;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FillChiseledBookshelfProcessor.class)
public class FillChiseledBookshelfProcessorMixin {
    @WrapOperation(method = "setChiseledBookshelfLoot", at = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/util/EnchantmentUtil;getRandomEnchantment(Lnet/minecraft/world/level/Level;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/core/Holder;"), remap = false)
    private Holder<Enchantment> DoltasticEnchantments$FilterRRs(Level level, RandomSource randomSource, Operation<Holder<Enchantment>> original) {
        if (DoltasticEnchantments.reliableRemover()) {
            Registry<Enchantment> enchantReg = level.registryAccess().registry(Registries.ENCHANTMENT).orElseThrow();
            return Util.getRandom(enchantReg.holders().filter(holder ->
                            !DEReliableRemoverCompat.isEnchantmentRemoved(holder.value()) &&
                            !ResourceUtil.isTag(holder, DETags.Enchantments.TREASURE, enchantReg)).toList(), randomSource);
        }
        return original.call(level, randomSource);
    }
}

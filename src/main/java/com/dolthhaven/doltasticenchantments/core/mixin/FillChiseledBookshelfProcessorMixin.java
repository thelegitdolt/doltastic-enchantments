package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.dolthhaven.doltasticenchantments.integration.emi.DEReliableRemoverCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.CostRegistry;
import me.alfie.immersiveenchanting.structure.FillChiseledBookshelfProcessor;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FillChiseledBookshelfProcessor.class)
public class FillChiseledBookshelfProcessorMixin {
    @WrapOperation(method = "finalizeProcessing", at = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/structure/FillChiseledBookshelfProcessor;rollLootForSlot(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/ItemStack;"), remap = false)
    private ItemStack DoltasticEnchantments$FilterRRs(FillChiseledBookshelfProcessor instance, RandomSource source, Operation<ItemStack> original, @Local(argsOnly = true) ServerLevelAccessor accessor) {
        var enchantReg = accessor.registryAccess().registry(Registries.ENCHANTMENT);
        if (enchantReg.isEmpty()) return original.call(instance, source);

        Holder<Enchantment> enchantment = Util.getRandom(CostRegistry.server().getAllEnchantmentHolders().stream().filter(holder ->
                        !ResourceUtil.isTag(holder, DETags.Enchantments.TREASURE, enchantReg.orElseThrow()) &&
                        !ResourceUtil.isTag(holder, DETags.Enchantments.DOESNT_REQUIRE_BOOK, enchantReg.orElseThrow()) &&
                        !(DoltasticEnchantments.reliableRemover() && DEReliableRemoverCompat.isEnchantmentRemoved(holder.value())))
                .toList(), source);
        return BookUtil.newBookWith(enchantment);
    }
}

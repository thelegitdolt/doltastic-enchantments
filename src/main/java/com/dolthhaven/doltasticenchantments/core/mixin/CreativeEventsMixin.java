package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.dolthhaven.doltasticenchantments.integration.emi.DEReliableRemoverCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alfie.immersiveenchanting.creativetab.ModCreativeTab;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ModCreativeTab.class)
public class CreativeEventsMixin {
    @WrapOperation(method = "buildCreativeTab", at = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/util/EnchantmentUtil;getAllRegisteredEnchantments(Lnet/minecraft/core/HolderLookup$Provider;)Ljava/util/List;"))
    private static List<Holder.Reference<Enchantment>> DoltasticEnchantments$HideReliablyRemovedBooks(HolderLookup.Provider lookup, Operation<List<Holder.Reference<Enchantment>>> original) {
        List<Holder.Reference<Enchantment>> enchantments = original.call(lookup);
        return enchantments.stream()
                .filter(holder -> {
                    if (DoltasticEnchantments.reliableRemover() && DEReliableRemoverCompat.isEnchantmentRemoved(holder.value())) {
                        return false;
                    } else return !ResourceUtil.isTag(holder, DETags.Enchantments.DOESNT_REQUIRE_BOOK, lookup);
                }).toList();
    }
}

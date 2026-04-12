package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.BookUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.networking.server.EnchantingTableServerHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(EnchantingTableServerHandler.class)
public class EnchantingTableServerHandlerMixin {
    // Makes it so that when an ancient book has multiple enchantments, the table will add them all.
    @WrapOperation(method = "checkBookshelvesAndUpdateClient",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/item/AncientBook;getStoredEnchantment(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/resources/ResourceKey;"),
                    to = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/networking/server/EnchantingTableServerHandler;isCreativeBookshelfNearby(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;)Z")),
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private static <E> boolean DoltasticEnchants$AddEveryEnchantmentOnABook(List<ResourceKey<Enchantment>> list, E e, Operation<Boolean> original, @Local ItemStack book) {
        return list.addAll(BookUtil.getAllStoredEnchantments(book));
    }
}

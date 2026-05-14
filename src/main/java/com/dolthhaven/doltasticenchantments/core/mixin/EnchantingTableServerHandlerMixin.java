package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
import me.alfie.immersiveenchanting.networking.server.EnchantingTableServerHandler;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
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

    @WrapOperation(method = "checkBookshelvesAndUpdateClient", at = @At(value = "NEW", target = "()Ljava/util/ArrayList;"), remap = false)
    private static ArrayList<ResourceKey<Enchantment>> hi(Operation<ArrayList<ResourceKey<Enchantment>>> original, @Local(argsOnly = true) Level level) {
        ArrayList<ResourceKey<Enchantment>> unlockedEnchants = original.call();
        EnchantmentUtil.getAllEnchantments(level).forEach(enchantment -> {
            if (EnchantmentCostRegistry.getRegistry(level).getEnchantmentCost(enchantment.key()) instanceof DefaultEnchantmentHolder holder) {
                if (!holder.requiresBook()) {
                    unlockedEnchants.add(enchantment.key());
                }
            }
        });

        return unlockedEnchants;
    }
}

package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.util.BookshelfChecker;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BookshelfChecker.class)
public class EnchantingTableServerHandlerMixin {
    // Makes it so that when an ancient book has multiple enchantments, the table will add them all.
    @WrapOperation(method = "getEnchantmentsInBookshelves",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private static <E> boolean DoltasticEnchants$AddEveryEnchantmentOnABook(List<ResourceKey<Enchantment>> list, E e, Operation<Boolean> original, @Local ItemStack book) {
        return list.addAll(BookUtil.getAllStoredEnchantments(book));
    }

    @ModifyReturnValue(method = "getEnchantmentsInBookshelves", at = @At(value = "RETURN"), remap = false)
    private static List<Holder<Enchantment>> hi(List<Holder<Enchantment>> originalHolders, @Local(argsOnly = true) Level level) {
        level.registryAccess().registry(Registries.ENCHANTMENT).ifPresent(reg ->
                reg.getTag(DETags.Enchantments.DOESNT_REQUIRE_BOOK).ifPresent(tag -> tag.forEach(originalHolders::add)));
        return originalHolders;
    }
}

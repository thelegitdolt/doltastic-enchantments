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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BookshelfChecker.class)
public class BookshelfCheckerMixin {
    // Automatically adds enchantments tagged as not needing books
    @ModifyReturnValue(method = "getEnchantmentsInBookshelves",
            at = @At(value = "RETURN"))
    private static List<Holder<Enchantment>> DoltasticEnchants$AddEnchantsThatDoesntRequireBooks(List<Holder<Enchantment>> original, @Local(argsOnly = true) Level level) {
        level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(DETags.Enchantments.DOESNT_REQUIRE_BOOKS)
                .ifPresent(named -> named.forEach(original::add));
        return original;
    }

    @WrapOperation(method = "getEnchantmentsInBookshelves",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static <E> boolean DoltasticEnchants$AddEveryEnchantmentOnABook(List<E> instance, E e, Operation<Boolean> original, @Local ItemStack ancientBook) {
        BookUtil.getAllStoredEnchantments(ancientBook).forEach(enchant -> {
            if (enchant != e) instance.add((E) enchant);
        });
        return original.call(instance, e);
    }
}

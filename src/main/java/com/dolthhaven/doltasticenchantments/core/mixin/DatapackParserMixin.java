package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import me.alfie.immersiveenchanting.datapack.parser.DatapackParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DatapackParser.class)
public class DatapackParserMixin {
    @Unique private static final String REQUIRE_BOOK = "requireBook";

    @ModifyReturnValue(method = "parseJson", at = @At(value = "RETURN"), remap = false)
    private static EnchantmentCost DoltasticEnchantments$AttachBookRequirement(EnchantmentCost original, @Local(ordinal = 0) JsonObject root) {
         boolean unlockedByDefault = true;

        if (root.has(REQUIRE_BOOK)) {
           unlockedByDefault = root.get(REQUIRE_BOOK).getAsBoolean();
        }

        if (original instanceof DefaultEnchantmentHolder defaultEnchantmentHolder) {
            defaultEnchantmentHolder.setRequiresBook(unlockedByDefault);
        }

        return original;
    }

    @ModifyReturnValue(method = "toJson", at = @At(value = "RETURN"), remap = false)
    private static JsonObject DoltasticEnchantments$AttachBookRequirement(JsonObject root, EnchantmentCost cost) {
        boolean requireBook = true;
        if (cost instanceof DefaultEnchantmentHolder defaultEnchantmentHolder) {
            defaultEnchantmentHolder.setRequiresBook(requireBook);
        }

        root.addProperty(REQUIRE_BOOK, requireBook);

        return root;
    }
}

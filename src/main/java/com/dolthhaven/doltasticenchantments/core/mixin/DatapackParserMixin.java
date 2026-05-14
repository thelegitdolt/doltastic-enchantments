package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.datapack.DefaultEnchantmentHolder;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.alfie.immersiveenchanting.datapack.cost.EnchantmentCost;
import me.alfie.immersiveenchanting.datapack.parser.DatapackParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DatapackParser.class)
public class DatapackParserMixin {
    @ModifyReturnValue(method = "toJson", at = @At(value = "RETURN"), remap = false)
    private static JsonObject DoltasticEnchantments$AttachBookRequirement(JsonObject root, EnchantmentCost cost) {
        boolean requireBook = true;
        if (cost instanceof DefaultEnchantmentHolder defaultEnchantmentHolder) {
            requireBook = defaultEnchantmentHolder.requiresBook();
        }

        root.addProperty(EnchantCostUtil.REQUIRES_BOOK, requireBook);

        return root;
    }
}

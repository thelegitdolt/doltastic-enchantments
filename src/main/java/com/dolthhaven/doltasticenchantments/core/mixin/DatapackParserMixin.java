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
    @Unique private static final String UNLOCKED_BY_DEFAULT = "unlockedByDefault";

    @ModifyReturnValue(method = "parseJson", at = @At(value = "RETURN"))
    private static EnchantmentCost DoltasticEnchantments$AttachBookRequirement(EnchantmentCost original, @Local(ordinal = 0) JsonObject root) {
        boolean unlockedByDefault = false;

        if (root.has(UNLOCKED_BY_DEFAULT)) {
           unlockedByDefault = root.get(UNLOCKED_BY_DEFAULT).getAsBoolean();
        }

        if (original instanceof DefaultEnchantmentHolder defaultEnchantmentHolder) {
            defaultEnchantmentHolder.setDefault(unlockedByDefault);
        }

        return original;
    }
}

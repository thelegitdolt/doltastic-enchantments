package com.dolthhaven.doltasticenchantments.core.mixin;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @Expression("? * ? + 1")
    @ModifyExpressionValue(method = "getDigSpeed", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private int DoltasticEnchants$ChangeEfficiencyBonus(int original) {
        int actualLevel = Math.round(Mth.sqrt(original - 1));
        return actualLevel * 7;
    }
}

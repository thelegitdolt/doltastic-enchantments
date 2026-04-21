package com.dolthhaven.doltasticenchantments.core.registry;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.AncientBookDiamondRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DERecipeSerializers {
    public static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, DoltasticEnchantments.MOD_ID);

    public static Supplier<RecipeSerializer<AncientBookDiamondRecipe>> ANCIENT_BOOK = RECIPE_SERIALIZERS
            .register("ancient_book", () -> new SimpleCraftingRecipeSerializer<>(AncientBookDiamondRecipe::new));
}

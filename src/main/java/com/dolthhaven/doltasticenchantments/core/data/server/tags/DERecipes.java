package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import com.dolthhaven.doltasticenchantments.core.registry.DERecipeSerializers;
import com.teamabnormals.blueprint.core.data.server.BlueprintRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.function.Consumer;

public class DERecipes extends BlueprintRecipeProvider {
    public DERecipes(GatherDataEvent event) {
        super(DoltasticEnchantments.MOD_ID, event.getGenerator().getPackOutput());
    }

    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DEItems.FAIRY_DUST.get(), 3)
                .requires(Ingredient.of(Items.DIAMOND, Items.POPPED_CHORUS_FRUIT))
                .requires(Ingredient.of(Items.COPPER_INGOT, Items.AMETHYST_SHARD, Items.GOLD_INGOT, Items.GLOWSTONE_DUST))
                .requires(Ingredient.of(Items.GLOW_INK_SAC, Items.PRISMARINE_CRYSTALS, Items.SCUTE, Items.NAUTILUS_SHELL))
                .requires(Ingredient.of(Items.BLAZE_POWDER, Items.ENDER_PEARL, Items.REDSTONE, Items.CHARCOAL, Items.COAL, Items.HONEYCOMB))
                .unlockedBy("has_lapis", has(Items.LAPIS_LAZULI)).save(consumer);
        SpecialRecipeBuilder.special(DERecipeSerializers.ANCIENT_BOOK.get()).save(consumer, "%s:ancient_book_from_diamonds".formatted(DoltasticEnchantments.MOD_ID));
    }
}

package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.AncientBookDiamondRecipe;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import com.teamabnormals.blueprint.core.data.server.BlueprintRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DERecipes extends BlueprintRecipeProvider {
    public DERecipes(GatherDataEvent event) {
        super(DoltasticEnchantments.MOD_ID, event.getGenerator().getPackOutput(), event.getLookupProvider());
    }

    public void buildRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DEItems.FAIRY_DUST.get(), 3)
                .requires(Ingredient.of(Items.DIAMOND, Items.POPPED_CHORUS_FRUIT))
                .requires(Ingredient.of(Items.COPPER_INGOT, Items.AMETHYST_SHARD, Items.GOLD_INGOT, Items.GLOWSTONE_DUST, Items.HONEYCOMB))
                .requires(Ingredient.of(Items.GLOW_INK_SAC, Items.PRISMARINE_CRYSTALS, Items.TURTLE_SCUTE, Items.NAUTILUS_SHELL))
                .requires(Ingredient.of(Items.BLAZE_POWDER, Items.MAGMA_CREAM, Items.ENDER_PEARL, Items.REDSTONE, Items.CHARCOAL, Items.COAL))
                .unlockedBy("has_lapis", has(Items.LAPIS_LAZULI)).save(output);
        SpecialRecipeBuilder.special(AncientBookDiamondRecipe::new).save(output, "%s:ancient_book_from_diamonds".formatted(DoltasticEnchantments.MOD_ID));
    }
}

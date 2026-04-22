package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.registry.DERecipeSerializers;
import com.teamabnormals.blueprint.core.data.server.BlueprintRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.function.Consumer;

public class DERecipes extends BlueprintRecipeProvider {
    public DERecipes(GatherDataEvent event) {
        super(DoltasticEnchantments.MOD_ID, event.getGenerator().getPackOutput());
    }

    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        SpecialRecipeBuilder.special(DERecipeSerializers.ANCIENT_BOOK.get()).save(consumer, "%s:ancient_book_from_diamonds".formatted(DoltasticEnchantments.MOD_ID));
    }
}

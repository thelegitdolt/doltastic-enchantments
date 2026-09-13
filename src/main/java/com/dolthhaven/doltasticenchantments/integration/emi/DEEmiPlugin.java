package com.dolthhaven.doltasticenchantments.integration.emi;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.AncientBookDiamondRecipe;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import me.alfie.immersiveenchanting.item.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@EmiEntrypoint
public class DEEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.setDefaultComparison(ModItems.ANCIENT_BOOK.get(), Comparison.compareComponents());

        for (RecipeHolder<CraftingRecipe> recipe : emiRegistry.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (recipe.value() instanceof AncientBookDiamondRecipe) {
                DoltasticEnchantments.LOGGER.info("Found diamond recipe! Adding Emi integration...");
                ReagentsRegistry.client().getRegister().forEach((enchant, ingredient) -> {
                    EmiIngredient reagent = toEmiIngredient(ingredient),
                                  diamond = EmiStack.of(Items.DIAMOND),
                                  book = EmiStack.of(Items.BOOK);
                    EmiStack ancientBook = EmiStack.of(BookUtil.newBookWith(enchant));

                    emiRegistry.addRecipe(new EmiCraftingRecipe(List.of(diamond, reagent, diamond, diamond, book, diamond, diamond, diamond, diamond), ancientBook,
                            recipe.id().withSuffix("/" + enchant.unwrapKey().orElseThrow().location().toString().replace(":", "_"))));
                });
                break;
            }
        }
    }

    private static EmiIngredient toEmiIngredient(CostHolder ingredient) {
        return EmiIngredient.of(EnchantCostUtil.map(ingredient, EmiStack::of, EmiIngredient::of));
    }
}

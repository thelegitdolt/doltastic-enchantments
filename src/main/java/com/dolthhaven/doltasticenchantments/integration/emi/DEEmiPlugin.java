package com.dolthhaven.doltasticenchantments.integration.emi;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.AncientBookDiamondRecipe;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import me.alfie.immersiveenchanting.item.AncientBook;
import me.alfie.immersiveenchanting.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@EmiEntrypoint
public class DEEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.setDefaultComparison(ModItems.ANCIENT_BOOK.get(), Comparison.compareNbt());

        for (CraftingRecipe recipe : getRecipes(emiRegistry, RecipeType.CRAFTING)) {
            if (recipe instanceof AncientBookDiamondRecipe) {
                DoltasticEnchantments.LOGGER.info("Found diamond recipe! Adding Emi integration...");
                ReagentsRegistry.client().getRegister().forEach((enchantKey, itemKey) -> {
                    Holder<Enchantment> enchant = EmiPort.getEnchantmentRegistry().getHolderOrThrow(enchantKey);
                    EmiStack reagent = EmiStack.of(EmiPort.getItemRegistry().get(itemKey)),
                             diamond = EmiStack.of(Items.DIAMOND),
                             book = EmiStack.of(Items.BOOK),
                             ancientBook = EmiStack.of(BookUtil.newBookWith(enchant));

                    emiRegistry.addRecipe(new EmiCraftingRecipe(List.of(diamond, reagent, diamond, diamond, book, diamond, diamond, diamond, diamond), ancientBook, recipe.getId()));
                });
                break;
            }
        }
    }

    private static <C extends Container, T extends Recipe<C>> Iterable<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        Stream<T> stream = registry.getRecipeManager().getAllRecipesFor(type).stream();
        Objects.requireNonNull(stream);
        return stream::iterator;
    }
}

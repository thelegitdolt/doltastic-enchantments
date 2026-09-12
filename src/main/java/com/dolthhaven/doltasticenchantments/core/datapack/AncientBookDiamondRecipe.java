package com.dolthhaven.doltasticenchantments.core.datapack;

import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.registry.DERecipeSerializers;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AncientBookDiamondRecipe extends CustomRecipe {
    public AncientBookDiamondRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.size() != 9 || input.width() != 3) return false;
        boolean matches =
                input.getItem(0).is(Items.DIAMOND) &&
                input.getItem(2).is(Items.DIAMOND) &&
                input.getItem(3).is(Items.DIAMOND) &&
                input.getItem(4).is(Items.BOOK) &&
                input.getItem(5).is(Items.DIAMOND) &&
                input.getItem(6).is(Items.DIAMOND) &&
                input.getItem(7).is(Items.DIAMOND) &&
                input.getItem(8).is(Items.DIAMOND);
        if (!matches) return false;
        return ReagentsRegistry.getRegistry(level).containsValue(input.getItem(1));
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ReagentsRegistry reagents = ReagentsRegistry.server();
        reagents = reagents.getRegister().isEmpty() ? reagents : ReagentsRegistry.client();

        return BookUtil.newBookWith(reagents.getValue(input.getItem(1)));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DERecipeSerializers.ANCIENT_BOOK.get();
    }
}

package com.dolthhaven.doltasticenchantments.core.datapack;

import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.registry.DERecipeSerializers;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class AncientBookDiamondRecipe extends CustomRecipe {
    public AncientBookDiamondRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        if (container.getContainerSize() != 9 || container.getWidth() != 3) return false;
        boolean matches =
                container.getItem(0).is(Items.DIAMOND) &&
                container.getItem(2).is(Items.DIAMOND) &&
                container.getItem(3).is(Items.DIAMOND) &&
                container.getItem(4).is(Items.BOOK) &&
                container.getItem(5).is(Items.DIAMOND) &&
                container.getItem(6).is(Items.DIAMOND) &&
                container.getItem(7).is(Items.DIAMOND) &&
                container.getItem(8).is(Items.DIAMOND);
        if (!matches) return false;
        return ReagentsRegistry.getRegistry(level).containsValue(container.getItem(1), level.registryAccess());
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ReagentsRegistry reagents = ReagentsRegistry.server();
        reagents = reagents.getRegister().isEmpty() ? reagents : ReagentsRegistry.client();
        Optional<Registry<Enchantment>> enchantments = access.registry(Registries.ENCHANTMENT);
        if (enchantments.isEmpty()) return ItemStack.EMPTY;

        return BookUtil.newBookWith(enchantments.orElseThrow()
                .getHolderOrThrow(reagents.getKey(container.getItem(1), access)));
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

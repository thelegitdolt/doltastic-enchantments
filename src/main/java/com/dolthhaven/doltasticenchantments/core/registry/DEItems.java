package com.dolthhaven.doltasticenchantments.core.registry;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.teamabnormals.blueprint.core.util.item.CreativeModeTabContentsPopulator;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import static net.minecraft.world.item.crafting.Ingredient.of;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = DoltasticEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DEItems {
    public static final ItemSubRegistryHelper HELPER = DoltasticEnchantments.REGISTRY_HELPER.getItemSubHelper();

    public static final RegistryObject<Item> FAIRY_DUST = HELPER.createItem("fairy_dust", () -> new Item(new Item.Properties()));

    public static void setUpTabEditors() {
        CreativeModeTabContentsPopulator.mod(DoltasticEnchantments.MOD_ID)
            .tab(CreativeModeTabs.INGREDIENTS).addItemsAfter(of(Items.LAPIS_LAZULI), FAIRY_DUST);
    }
}

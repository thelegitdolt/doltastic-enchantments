package com.dolthhaven.doltasticenchantments.core;

import com.dolthhaven.doltasticenchantments.client.gui.ClientEvents;
import com.dolthhaven.doltasticenchantments.core.data.client.DEItemsModelsGen;
import com.dolthhaven.doltasticenchantments.core.data.server.DELootModifiersProvider;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DEEnchantmentTags;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DERecipes;
import com.dolthhaven.doltasticenchantments.core.networking.DEPackets;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import com.dolthhaven.doltasticenchantments.core.registry.DELoot;
import com.dolthhaven.doltasticenchantments.core.registry.DERecipeSerializers;
import com.mojang.logging.LogUtils;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(DoltasticEnchantments.MOD_ID)
public class DoltasticEnchantments {
    public static final String MOD_ID = "doltastic_enchantments";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public DoltasticEnchantments() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus bus = context.getModEventBus();

        REGISTRY_HELPER.register(bus);
        DELoot.LOOT_MODIFIERS.register(bus);
        DERecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        DEPackets.register();

        bus.addListener(this::commonSetup);
        bus.addListener(this::dataSetup);
        bus.addListener(ClientEvents::registerInternalEnchantingTooltips);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DEItems::setUpTabEditors);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    public static boolean reliableRemover() {
        return ModList.get().isLoaded("reliable_remover");
    }

    private void dataSetup(final GatherDataEvent event) {
        DataGenerator dataGen = event.getGenerator();
        boolean server = event.includeServer();
        dataGen.addProvider(server, new DEEnchantmentTags(event));
        dataGen.addProvider(server, new DERecipes(event));
        dataGen.addProvider(server, new DELootModifiersProvider(event));

        boolean client = event.includeClient();
        dataGen.addProvider(client, new DEItemsModelsGen(event));
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static Component translatable(String key) {
        return Component.translatable(key.formatted(MOD_ID));
    }
}

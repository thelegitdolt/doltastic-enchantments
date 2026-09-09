package com.dolthhaven.doltasticenchantments.core;

import com.dolthhaven.doltasticenchantments.client.ClientEvents;
import com.dolthhaven.doltasticenchantments.core.data.client.DEItemsModelsGen;
import com.dolthhaven.doltasticenchantments.core.data.server.DELootRemolder;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

@Mod(DoltasticEnchantments.MOD_ID)
public class DoltasticEnchantments {
    public static final String MOD_ID = "doltastic_enchantments";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public DoltasticEnchantments(ModContainer container) {
        IEventBus bus = container.getEventBus();

        DEItems.ITEMS.register(bus);

        DELoot.LOOT_MODIFIERS.register(bus);
        DERecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        DEPackets.register();

        bus.addListener(this::commonSetup);
        bus.addListener(this::dataSetup);
        bus.addListener(ClientEvents::registerInternalEnchantingTooltips);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            DEItems.setUpTabEditors();
        }

        NeoForge.EVENT_BUS.register(this);
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
        dataGen.addProvider(server, new DELootRemolder(event));

        boolean client = event.includeClient();
        dataGen.addProvider(client, new DEItemsModelsGen(event));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Component translatable(String key) {
        return Component.translatable(key.formatted(MOD_ID));
    }
}

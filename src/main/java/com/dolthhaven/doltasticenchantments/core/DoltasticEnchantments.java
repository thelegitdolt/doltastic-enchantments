package com.dolthhaven.doltasticenchantments.core;

import com.mojang.logging.LogUtils;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(DoltasticEnchantments.MOD_ID)
public class DoltasticEnchantments {
    public static final String MOD_ID = "doltastic_enchantments";
    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);
    private static final Logger LOGGER = LogUtils.getLogger();

    public DoltasticEnchantments() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus bus = context.getModEventBus();

        REGISTRY_HELPER.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::dataSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DEItems::setUpTabEditors);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void dataSetup(final GatherDataEvent event) {
        DataGenerator dataGen = event.getGenerator();
        boolean server = event.includeServer();

        boolean client = event.includeClient();
        dataGen.addProvider(client, new DEItemsModelsGen(event));
    }
}

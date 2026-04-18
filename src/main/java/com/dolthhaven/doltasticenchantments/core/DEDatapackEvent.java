package com.dolthhaven.doltasticenchantments.core;

import com.dolthhaven.doltasticenchantments.core.datapack.reagents.EnchantReagentDatapack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DoltasticEnchantments.MOD_ID)
public class DEDatapackEvent {
    @SubscribeEvent
    public static void setDatapackServer(ServerStartedEvent event) {
        EnchantReagentDatapack.DATAPACK.setServer(event.getServer());
    }

    @SubscribeEvent
    public static void addInModDatapack(AddReloadListenerEvent event) {
        event.addListener(EnchantReagentDatapack.DATAPACK);
    }
}

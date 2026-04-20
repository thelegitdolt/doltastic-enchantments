package com.dolthhaven.doltasticenchantments.core;

import com.dolthhaven.doltasticenchantments.core.datapack.reagents.EnchantReagentDatapack;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantReagentSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DoltasticEnchantments.MOD_ID)
public class DEDatapackEvent {
    @SubscribeEvent
    public static void setDatapackServer(ServerStartedEvent event) {
        EnchantReagentDatapack.DATAPACK.setServer(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addInModDatapack(AddReloadListenerEvent event) {
        EnchantReagentDatapack.DATAPACK.setAccess(event.getRegistryAccess());
        event.addListener(EnchantReagentDatapack.DATAPACK);
    }

    @SubscribeEvent
    public static void syncClientRegistry(PlayerEvent.PlayerLoggedInEvent event) {
        Player var2 = event.getEntity();
        if (var2 instanceof ServerPlayer player) {
            EnchantReagentSyncPacket.sync(player);
        }
    }
}

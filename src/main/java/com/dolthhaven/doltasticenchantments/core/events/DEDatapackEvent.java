package com.dolthhaven.doltasticenchantments.core.events;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.EnchantReagentDatapack;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantReagentSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = DoltasticEnchantments.MOD_ID)
public class DEDatapackEvent {
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

    @SubscribeEvent
    public static void finalizeCosts(TagsUpdatedEvent event) {
        ReagentsRegistry server = ReagentsRegistry.server();
        if (server != null && !server.getRegister().isEmpty()) {
            server.expandTags();
        }

        ReagentsRegistry client = ReagentsRegistry.client();
        if (client != null && !client.getRegister().isEmpty()) {
            client.expandTags();
        }
    }
}

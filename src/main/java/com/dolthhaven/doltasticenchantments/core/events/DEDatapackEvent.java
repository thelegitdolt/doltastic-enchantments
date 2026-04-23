package com.dolthhaven.doltasticenchantments.core.events;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.EnchantReagentDatapack;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantReagentSyncPacket;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = DoltasticEnchantments.MOD_ID)
public class DEDatapackEvent {
    @SubscribeEvent
    public static void setDatapackServer(ServerStartedEvent event) {
        List<ResourceLocation> rlList = new ArrayList<>(24);
        event.getServer().getLootData().elements.forEach((lootDataId, o) -> {
            if (lootDataId.type() == LootDataType.TABLE) {
                if ((lootDataId.location().getPath().contains("chests/"))) rlList.add(lootDataId.location());
            }
        });
        DoltasticEnchantments.LOGGER.info("HELLO from server starting. Also here are all the chest loot tables: {}", EnchantCostUtil.reduceToString(rlList, Function.identity(), ", "));
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

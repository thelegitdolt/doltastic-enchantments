package com.dolthhaven.doltasticenchantments.core.networking;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("removal")
public class EnchantmentReagentSyncPacket {
    private final List<String> enchants;
    private final List<String> items;

    public EnchantmentReagentSyncPacket(List<String> enchants, List<String> items) {
        this.enchants = enchants;
        this.items = items;
    }

    public static void encode(EnchantmentReagentSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.enchants, FriendlyByteBuf::writeUtf);
        buf.writeCollection(packet.items, FriendlyByteBuf::writeUtf);
    }

    public static EnchantmentReagentSyncPacket decode(FriendlyByteBuf buf) {
        List<String> enchants = buf.readList(FriendlyByteBuf::readUtf);
        List<String> items = buf.readList(FriendlyByteBuf::readUtf);
        return new EnchantmentReagentSyncPacket(enchants, items);
    }

    public static void handle(EnchantmentReagentSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DoltasticEnchantments.LOGGER.info("EnchantmentReagentSyncPacket packet received on client, syncing...");
            packet.populateClientRegistry();
        });
        contextSupplier.get().setPacketHandled(true);
    }

    private void populateClientRegistry() {
        if (enchants.size() != items.size()) {
            throw new IllegalStateException("Enchants and items do not have the same size, cannot deserialize");
        }
        ReagentsRegistry registry = ReagentsRegistry.client();
        registry.clear();

        for (int i  = 0; i < enchants.size(); i++) {
            registry.put(new ResourceLocation(enchants.get(i)), new ResourceLocation(items.get(i)));
        }
    }

    public static void sync(ServerPlayer player) {
        if (!player.level().isClientSide) {
            Pair<List<String>, List<String>> entryPair = ReagentsRegistry.server().encode();
            DEPackets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new EnchantmentReagentSyncPacket(entryPair.getFirst(), entryPair.getSecond()));
        }
    }
}

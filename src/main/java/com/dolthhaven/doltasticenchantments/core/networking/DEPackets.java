package com.dolthhaven.doltasticenchantments.core.networking;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class DEPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(DoltasticEnchantments.rl("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, EnchantmentReagentSyncPacket.class, EnchantmentReagentSyncPacket::encode, EnchantmentReagentSyncPacket::decode, EnchantmentReagentSyncPacket::handle);
    }
}

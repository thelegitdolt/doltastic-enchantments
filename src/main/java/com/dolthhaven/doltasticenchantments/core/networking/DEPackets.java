package com.dolthhaven.doltasticenchantments.core.networking;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class DEPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(DoltasticEnchantments.rl("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, EnchantReagentSyncPacket.class, EnchantReagentSyncPacket::encode, EnchantReagentSyncPacket::decode, EnchantReagentSyncPacket::handle);
        INSTANCE.registerMessage(id++, ConjurePacket.class, ConjurePacket::encode, ConjurePacket::decode, ConjurePacket::handle);
    }
}

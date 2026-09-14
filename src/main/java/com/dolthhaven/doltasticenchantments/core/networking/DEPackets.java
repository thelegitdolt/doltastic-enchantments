package com.dolthhaven.doltasticenchantments.core.networking;

import me.alfie.alfinolib.networking.NetworkRegisterEvent;
import me.alfie.alfinolib.networking.Networking;

public class DEPackets {
    public static void register(NetworkRegisterEvent event) {
        event.register(Networking.Side.SERVER, ConjurePacket.TYPE, ConjurePacket.STREAM_CODEC);
    }
}

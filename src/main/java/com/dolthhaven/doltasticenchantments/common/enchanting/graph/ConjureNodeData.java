package com.dolthhaven.doltasticenchantments.common.enchanting.graph;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.networking.ConjurePacket;
import me.alfie.alfinolib.networking.Networking;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.api.node.NodeData;
import me.alfie.immersiveenchanting.api.node.NodePayload;

public record ConjureNodeData() implements NodePayload {
    public static final ResourceId TYPE = DoltasticEnchantments.rid("conjure");

    @Override
    public ResourceId type() {
        return TYPE;
    }

    public static NodeData<ConjureNodeData> create() {
        return new NodeData<>(TYPE, new ConjureNodeData(), (data, context) ->
                Networking.sendToServer(new ConjurePacket("sex")));
    }
}

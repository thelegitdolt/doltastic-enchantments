package com.dolthhaven.doltasticenchantments.common.enchanting.graph;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.networking.ConjurePacket;
import me.alfie.alfinolib.networking.Networking;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.api.node.NodeData;
import me.alfie.immersiveenchanting.api.node.NodePayload;

import static java.util.Objects.isNull;

public record ConjureNodeData(ResourceId enchantmentId) implements NodePayload {
    public static final ResourceId TYPE = DoltasticEnchantments.rid("conjure");
    public static final String FLAG = "/conjuring";
    public static final ResourceId EMPTY = DoltasticEnchantments.rid("conjure_empty");

    @Override
    public ResourceId type() {
        return TYPE;
    }

    public static NodeData<ConjureNodeData> create(ResourceId enchantment) {
        ResourceId withSuffix = isNull(enchantment) ? EMPTY : new ResourceId(enchantment.namespace(), enchantment.path() + FLAG);
        return new NodeData<>(TYPE, new ConjureNodeData(withSuffix), (data, context) ->
                Networking.sendToServer(new ConjurePacket(enchantment.mc())));
    }

    public static ResourceId unwrapFlag(ResourceId id) {
        return new ResourceId(id.namespace(), id.path().substring(0, id.path().length() - FLAG.length()));
    }
}

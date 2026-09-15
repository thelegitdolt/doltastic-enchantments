package com.dolthhaven.doltasticenchantments.core.mixin.nodesoundpatch;

import com.dolthhaven.doltasticenchantments.common.enchanting.graph.ConjureNodeData;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.datapack.node_sounds.NodeSound;
import me.alfie.immersiveenchanting.datapack.node_sounds.NodeSoundMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NodeSoundMap.class)
public abstract class NodeSoundMapMixin {
    @Shadow public abstract boolean containsKey(ResourceId id);

    @Shadow public abstract NodeSound get(ResourceId id);

    @ModifyReturnValue(method = "containsKey", at = @At(value = "RETURN"))
    private boolean DoltasticEnchantments$TryFindConjuringNodesAlso(boolean original, @Local(argsOnly = true) ResourceId id) {
        if (!original && id.path().endsWith(ConjureNodeData.FLAG)) {
            return this.containsKey(ConjureNodeData.unwrapFlag(id));
        }
        return original;
    }

    @ModifyReturnValue(method = "get", at = @At(value = "RETURN"))
    private NodeSound DoltasticEnchantments$TryFindConjuringNodesAlso(NodeSound original, @Local(argsOnly = true) ResourceId id) {
        if (original == null && id.path().endsWith(ConjureNodeData.FLAG)) {
            return this.get(ConjureNodeData.unwrapFlag(id));
        }
        return original;
    }
}

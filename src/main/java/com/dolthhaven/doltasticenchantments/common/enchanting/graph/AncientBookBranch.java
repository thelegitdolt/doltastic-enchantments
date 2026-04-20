package com.dolthhaven.doltasticenchantments.common.enchanting.graph;

import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeBranch;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeType;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public class AncientBookBranch extends NodeBranch {
    private final Holder<Enchantment> enchantment;
    private final boolean unlocked;

    public AncientBookBranch(EnchantingTableScreen screen, float branchAngle, Holder<Enchantment> enchantment, boolean unlocked) {
        super(screen, branchAngle);
        this.enchantment = enchantment;
        this.unlocked = unlocked;

        this.addNode(new AncientBookNode(NodeType.ELITE, enchantment, unlocked));
    }

    public Holder<Enchantment> getEnchantment() {
        return enchantment;
    }
}

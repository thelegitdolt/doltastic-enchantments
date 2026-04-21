package com.dolthhaven.doltasticenchantments.common.enchanting.graph;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.networking.ConjurePacket;
import com.dolthhaven.doltasticenchantments.core.networking.DEPackets;
import me.alfie.immersiveenchanting.datapack.EnchantmentMetadataRegistry;
import me.alfie.immersiveenchanting.datapack.cost.CostEntry;
import me.alfie.immersiveenchanting.gui.EnchantingTableScreen;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.Node;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeTooltip;
import me.alfie.immersiveenchanting.gui.core.tab.enchanting.node.NodeType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("removal")
public class AncientBookNode extends Node {
    @Nullable
    private final Holder<Enchantment> enchantment;
    private boolean unlocked;

    public AncientBookNode(NodeType nodeType, @Nullable Holder<Enchantment> enchantment, boolean unlocked) {
        super(nodeType, getSprite(enchantment, unlocked));
        this.enchantment = enchantment;
        this.unlocked = unlocked;
        if (!unlocked) {
            this.setNodeType(NodeType.LOCKED);
        }
    }

    @Override
    public boolean onClicked(int x, int y) {
        if (this.isUnlocked()) {
            DEPackets.INSTANCE.sendToServer(new ConjurePacket(this.enchantment.unwrapKey().orElseThrow().location().toString()));
            return true;
        } else {
            return false;
        }
    }

    private static ResourceLocation getSprite(@Nullable Holder<Enchantment> enchantment, boolean unlocked) {
        if (enchantment == null && !unlocked) return null;

        Objects.requireNonNull(enchantment);
        ResourceLocation enchantLoc = enchantment.unwrapKey().orElseThrow().location();
        if (EnchantmentMetadataRegistry.getIcons().containsKey(enchantLoc)) {
            return EnchantmentMetadataRegistry.getIconTexture(enchantLoc);
        } else {
            return new ResourceLocation("immersiveenchanting", "textures/item/ancient_book.png");
        }
    }

    public Holder<Enchantment> getEnchantment() {
        return enchantment;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public static class AncientNodeTooltips extends NodeTooltip {
        public AncientNodeTooltips(AncientBookNode node, EnchantingTableScreen screen) {
            super(node, screen, List.of(costToRender(screen, node)));
            this.tooltipTitle.setTitleText(getTitleText(node));
        }

        private static Component getTitleText(AncientBookNode node) {
            if (node.enchantment == null && !node.isUnlocked()) {
                return DoltasticEnchantments.translatable("gui.%s.conjure");
            }

            Objects.requireNonNull(node.enchantment);
            MutableComponent loreText = Component.translatable("lore.immersiveenchanting.ancient_book");
            String enchantName = node.getEnchantment().value().getDescriptionId();
            return loreText.append(" ").append(Component.translatable(enchantName));
        }

        private static String getReagent(Level level, AncientBookNode node) {
            if (node.enchantment != null) {
                ResourceKey<Item> item = ReagentsRegistry.getRegistry(level).get(node.enchantment.unwrapKey().orElseThrow());
                if (item != null)
                    return item.location().toString();
            }
            return "minecraft:air";
        }

        public static CostEntry costToRender(EnchantingTableScreen screen, Node node) {
            return new CostEntry(getReagent(screen.player.level(), (AncientBookNode) node), "", 1, 20);
        }
    }
}

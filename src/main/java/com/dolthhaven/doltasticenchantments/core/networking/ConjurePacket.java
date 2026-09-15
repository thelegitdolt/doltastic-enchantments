package com.dolthhaven.doltasticenchantments.core.networking;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import me.alfie.alfinolib.networking.NetworkPacket;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.Cost;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostData;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import me.alfie.immersiveenchanting.util.CostHelper;
import me.alfie.immersiveenchanting.util.FxHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public record ConjurePacket(ResourceLocation enchantment) implements NetworkPacket<ConjurePacket> {
    public static final CustomPacketPayload.Type<ConjurePacket> TYPE = new CustomPacketPayload.Type<>(DoltasticEnchantments.rl("replicate"));
    public static StreamCodec<RegistryFriendlyByteBuf, ConjurePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, ConjurePacket replicatePacket) {
            buf.writeResourceLocation(replicatePacket.enchantment);
        }

        @Override
        public ConjurePacket decode(RegistryFriendlyByteBuf buf) {
            return new ConjurePacket(buf.readResourceLocation());
        }
    };

    @Override
    public CustomPacketPayload.Type<ConjurePacket> type() {
        return TYPE;
    }

    @Override
    public void exec(IPayloadContext ctx) {
        Player player = ctx.player();
        ResourceKey<Enchantment> enchantKey = ResourceUtil.enchant(this.enchantment());
        Registry<Enchantment> registry = player.level().registryAccess().registry(Registries.ENCHANTMENT).orElseThrow();

        if (player.containerMenu instanceof EnchantingTableMenu menu) {
            ItemStack toolStack = menu.getToolSlot().getItem();
            Holder<Enchantment> enchantRef = registry.getHolderOrThrow(enchantKey);
            CostHolder cost = ReagentsRegistry.server().get(enchantRef);

            Cost validCost = tryDeductCost(cost, player, menu);
            if (validCost != null) {
                ItemStack book = BookUtil.newBookWith(enchantRef);
                BookUtil.drop(player, toolStack.split(toolStack.getCount() - 1));
                menu.getToolSlot().set(book);
                menu.getToolSlot().setChanged();

                player.awardStat(Stats.ENCHANT_ITEM);
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayer, toolStack, validCost.xpLevels());
                }
                FxHelper.playEnchantSuccess(player, menu.getBlockPos(), false);
            }
        }
    }

    // only deduct costs. does not assemble
    private static Cost tryDeductCost(CostHolder costHolder, Player player, EnchantingTableMenu menu) {
        ItemStack toolStack = menu.getToolSlot().getItem();
        ItemStack reagentStack = menu.getCostSlot().getItem();

        if (!toolStack.is(DETags.Items.CONJURE_BASES)) return null;
        if (player.hasInfiniteMaterials()) return Cost.EMPTY;

        Cost validCost = null;
        for (Cost cost : costHolder.costs()) {
            if (cost.test(reagentStack, player) != null) {
                validCost = cost;
                break;
            }
        }
        if (validCost == null) return null;

        if (!validCost.getItemStacks().contains(Items.AIR.getDefaultInstance())) {
            reagentStack.shrink(validCost.itemCost().count());
        }
        player.giveExperienceLevels(-validCost.xpLevels());
        return validCost;
    }
}


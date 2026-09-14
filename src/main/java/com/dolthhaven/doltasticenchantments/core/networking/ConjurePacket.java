package com.dolthhaven.doltasticenchantments.core.networking;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import me.alfie.alfinolib.networking.NetworkPacket;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConjurePacket(String enchantment) implements NetworkPacket<ConjurePacket> {
    public static final CustomPacketPayload.Type<ConjurePacket> TYPE = new CustomPacketPayload.Type<>(DoltasticEnchantments.rl("replicate"));
    public static StreamCodec<RegistryFriendlyByteBuf, ConjurePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, ConjurePacket replicatePacket) {

        }

        @Override
        public ConjurePacket decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            return new ConjurePacket("sex");
        }
    };

    @Override
    public CustomPacketPayload.Type<ConjurePacket> type() {
        return TYPE;
    }

    @Override
    public void exec(IPayloadContext ctx) {
//        ServerPlayer player = ctx.player();
//        ResourceKey<Enchantment> enchantKey = ResourceUtil.senchant(enchantmentString);
//        Optional<Registry<Enchantment>> registry = player.level().registryAccess().registry(Registries.ENCHANTMENT);
//        if (registry.isEmpty()) return;
//
//        if (player.containerMenu instanceof EnchantingTableMenu menu) {
//            ItemStack toolStack = menu.getToolSlotItem();
//            ItemStack reagentStack = menu.getEnchantingFuelSlotItem();
//            if (!toolStack.is(Items.BOOK)) return;
//
//            CostEntry costToDo = CostHelper.findValidCost(getCost(enchantKey), List.of(reagentStack), player.experienceLevel);
//            if (player.isCreative() || CostHelper.isCostValid(costToDo)) {
//                ItemStack book = BookUtil.newBookWith(registry.orElseThrow().getHolderOrThrow(enchantKey));
//                if (player.isCreative()) costToDo = CostEntry.EMPTY;
//                CostHelper.deductCost(TWENTY_LEVELS, costToDo, ItemStack.EMPTY, reagentStack, player);
//
//                BookUtil.drop(player, toolStack.split(toolStack.getCount() - 1));
//                player.containerMenu.getSlot(EnchantingTableMenu.SLOTS.TOOL.ordinal()).set(book);
//
//                player.awardStat(Stats.ENCHANT_ITEM);
//                CriteriaTriggers.ENCHANTED_ITEM.trigger(player, toolStack, costToDo.xpLevels());
//                FxHelper.playEnchantSuccessFx(player.level(), menu.getBlockPos(), false);
//            } else {
//                FxHelper.playEnchantFailFx(player.level(), menu.getBlockPos());
//            }
//        }
    }
}


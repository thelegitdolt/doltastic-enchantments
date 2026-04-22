package com.dolthhaven.doltasticenchantments.core.networking;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.datapack.reagents.ReagentsRegistry;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import me.alfie.immersiveenchanting.datapack.cost.CostDefinition;
import me.alfie.immersiveenchanting.datapack.cost.CostEntry;
import me.alfie.immersiveenchanting.gui.EnchantingTableMenu;
import me.alfie.immersiveenchanting.util.CostHelper;
import me.alfie.immersiveenchanting.util.FxHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ConjurePacket {
    private static final CostEntry TWENTY_LEVELS = new CostEntry("minecraft:air", "", 0, 20);
    private final String enchant;

    public ConjurePacket(String enchant) {
        this.enchant = enchant;
    }

    public static void encode(ConjurePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.enchant);
    }

    public static ConjurePacket decode(FriendlyByteBuf buf) {
        String enchant = buf.readUtf();
        return new ConjurePacket(enchant);
    }

    public static void handle(ConjurePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DoltasticEnchantments.LOGGER.info("ConjurePacket packet received from client, conjuring...");
            exec(contextSupplier.get(), packet.enchant);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    private static void exec(NetworkEvent.Context ctx, String enchantmentString) {
        ServerPlayer player = ctx.getSender();
        ResourceKey<Enchantment> enchantKey = ResourceKeyUtil.senchant(enchantmentString);
        Optional<Registry<Enchantment>> registry = player.level().registryAccess().registry(Registries.ENCHANTMENT);
        if (registry.isEmpty()) return;

        if (player.containerMenu instanceof EnchantingTableMenu menu) {
            ItemStack toolStack = menu.getToolSlotItem();
            ItemStack reagentStack = menu.getEnchantingFuelSlotItem();
            if (!toolStack.is(Items.BOOK)) return;

            CostEntry costToDo = CostHelper.findValidCost(getCost(enchantKey), List.of(reagentStack), player.experienceLevel);
            if (player.isCreative() || CostHelper.isCostValid(costToDo)) {
                ItemStack book = BookUtil.newBookWith(registry.orElseThrow().getHolderOrThrow(enchantKey));
                if (player.isCreative()) costToDo = CostEntry.EMPTY;
                CostHelper.deductCost(TWENTY_LEVELS, costToDo, ItemStack.EMPTY, reagentStack, player);

                BookUtil.drop(player, toolStack.split(toolStack.getCount() - 1));
                player.containerMenu.getSlot(EnchantingTableMenu.SLOTS.TOOL.ordinal()).set(book);

                player.awardStat(Stats.ENCHANT_ITEM);
                CriteriaTriggers.ENCHANTED_ITEM.trigger(player, toolStack, costToDo.xpLevels());
                FxHelper.playEnchantSuccessFx(player.level(), menu.getBlockPos(), false);
            } else {
                FxHelper.playEnchantFailFx(player.level(), menu.getBlockPos());
            }
        }
    }

    private static @NotNull CostDefinition getCost(ResourceKey<Enchantment> enchantKey) {
        return ReagentsRegistry.server().get(enchantKey).cost();
    }

}


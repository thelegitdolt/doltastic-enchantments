package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import me.alfie.alfinolib.datapacks.client.ClientDatapackManager;
import me.alfie.alfinolib.datapacks.server.ServerDatapackManager;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import me.alfie.alfinolib.util.ResourceId;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReagentsRegistry {
    public static final ResourceId CONJURE_ID = DoltasticEnchantments.rid("conjure");

    private Map<Holder<Enchantment>, CostHolder> register = new HashMap<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, ReagentsRegistry> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, ReagentsRegistry registry) {
            buf.writeInt(registry.register.size());
            for (var entry : registry.register.entrySet()) {
                buf.writeResourceLocation(entry.getKey().unwrapKey().orElseThrow().location());
                CostHolder.STREAM_CODEC.encode(buf, entry.getValue());
            }
        }

        @Override
        public ReagentsRegistry decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readInt();
            ReagentsRegistry reagentRegistry = new ReagentsRegistry();
            Registry<Enchantment> reg = buf.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            for (int i = 0; i < size; i++) {
                ResourceLocation rl = buf.readResourceLocation();
                CostHolder data = CostHolder.STREAM_CODEC.decode(buf);
                reagentRegistry.register.put(reg.getHolder(rl).orElseThrow(), data);
            }
            return reagentRegistry;
        }
    };

    public static ReagentsRegistry client() {
        return ClientDatapackManager.get(ReagentDatapack.KEY);
    }

    public static ReagentsRegistry server() {
        return ServerDatapackManager.get(ReagentDatapack.KEY);
    }

    public void clear() {
        register.clear();
    }

    public static ReagentsRegistry getRegistry(Level level) {
        return level.isClientSide ? client() : server();
    }

    public CostHolder get(Holder<Enchantment> enchantment) {
        if (!register.containsKey(enchantment)) return null;
        return register.get(enchantment);
    }

    public Holder<Enchantment> getValue(ItemStack stack) {
        for (var enchantAndCost : register.entrySet()) {
            if (EnchantCostUtil.test(enchantAndCost.getValue(), stack)) {
                return enchantAndCost.getKey();
            }
        }
        return null;
    }

    /**
     * I AM NOT REWORKING MY ENTIRE MOD FOR THIS SETBACK
     */
    public <T> CostHolder findWithoutRegAccess(T find, Function<Holder<Enchantment>, T> interpreter) {
        Holder<Enchantment> holder = null;

        for (Holder<Enchantment> holderInReg : register.keySet()) {
            if (interpreter.apply(holderInReg).equals(find)) {
                holder = holderInReg;
                break;
            }
        }
        if (holder != null) {
            return register.get(holder);
        }
        return null;
    }

    public CostHolder put(Holder<Enchantment> enchantment, CostHolder item) {
        return register.put(enchantment, item);
    }

    public boolean containsKey(Holder<Enchantment> enchantment) {
        return register.containsKey(enchantment);
    }

    public boolean containsValue(ItemStack stack) {
        return register.values().stream().anyMatch(cost -> EnchantCostUtil.test(cost, stack));
    }

    public Map<Holder<Enchantment>, CostHolder> getRegister() {
        return register;
    }
}

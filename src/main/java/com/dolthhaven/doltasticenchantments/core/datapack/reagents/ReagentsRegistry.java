package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.utils.CostHolderUtils;
import me.alfie.alfinolib.datapacks.client.ClientDatapackManager;
import me.alfie.alfinolib.datapacks.server.ServerDatapackManager;
import me.alfie.alfinolib.networking.codec.StreamCodec;
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

public class ReagentsRegistry {
    private final Map<Holder<Enchantment>, CostHolder> register = new HashMap<>();
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
            if (CostHolderUtils.test(enchantAndCost.getValue(), stack)) {
                return enchantAndCost.getKey();
            }
        }
        return null;
    }

    public CostHolder getUnsafe(Holder<Enchantment> enchantment) {
        return register.get(enchantment);
    }

    public CostHolder put(Holder<Enchantment> enchantment, CostHolder item) {
        return register.put(enchantment, item);
    }

    public boolean containsKey(Holder<Enchantment> enchantment) {
        return register.containsKey(enchantment);
    }

    public boolean containsValue(ItemStack stack) {
        return register.values().stream().anyMatch(cost -> CostHolderUtils.test(cost, stack));
    }

    public Map<Holder<Enchantment>, CostHolder> getRegister() {
        return register;
    }

//    public void expandTags() {
//        Set<Pair<ResourceKey<Enchantment>, CostHolder>> updatedTags = new HashSet<>();
//        for (var iterator = register.entrySet().iterator(); iterator.hasNext(); ) {
//            Map.Entry<ResourceKey<Enchantment>, CostHolder> entry = iterator.next();
//            CostHolder ingredient = entry.getValue();
//            if (ingredient.isTag()) {
//                Optional<HolderSet.Named<Item>> tags = BuiltInRegistries.ITEM.getTag(ingredient.tag());
//                if (tags.isEmpty() || tags.orElseThrow().size() == 0) {
//                    DoltasticEnchantments.LOGGER.info("Associated empty tag {} as reagent of enchantment {}, this is an error in your scripts", ingredient.tag().location(), entry.getKey());
//                } else {
//                    iterator.remove();
//                    updatedTags.add(Pair.of(entry.getKey(), new BasicIngredient(tags.orElseThrow().stream().map(a ->
//                            EnchantCostUtil.basicCost(a.unwrapKey().orElseThrow().location().toString(), BasicIngredient.CONJURE_XP_COST)).toList())));
//                }
//            }
//        }
//        updatedTags.forEach(pair -> this.put(pair.getFirst(), pair.getSecond()));
//    }
}

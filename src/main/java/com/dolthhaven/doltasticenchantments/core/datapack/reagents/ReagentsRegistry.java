package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.*;

public class ReagentsRegistry {
    private static final ReagentsRegistry CLIENT =  new ReagentsRegistry();
    private static final ReagentsRegistry SERVER_REGISTRY =  new ReagentsRegistry();

    private final Map<ResourceKey<Enchantment>, BasicIngredient> register = new HashMap<>();

    public static ReagentsRegistry client() {
        return CLIENT;
    }

    public static ReagentsRegistry server() {
        return SERVER_REGISTRY;
    }

    public void clear() {
        register.clear();
    }

    public static ReagentsRegistry getRegistry(Level level) {
        return level.isClientSide ? client() : server();
    }

    public BasicIngredient get(ResourceKey<Enchantment> enchantment) {
        if (!register.containsKey(enchantment)) return BasicIngredient.EMPTY;
        return register.get(enchantment);
    }

    public BasicIngredient getUnsafe(Holder<Enchantment> enchantment) {
        return register.get(enchantment.unwrapKey().orElseThrow());
    }

    public BasicIngredient put(ResourceKey<Enchantment> enchantment, BasicIngredient item) {
        return register.put(enchantment, item);
    }

    public BasicIngredient put(ResourceLocation enchantment, BasicIngredient item) {
        return put(ResourceKeyUtil.enchant(enchantment), item);
    }


    public boolean containsKey(ResourceKey<Enchantment> enchantment) {
        return register.containsKey(enchantment);
    }

    public boolean containsValue(ItemStack stack) {
        return register.values().stream().anyMatch(ing -> ing.test(stack));
    }

    public Map<ResourceKey<Enchantment>, BasicIngredient> getRegister() {
        return register;
    }

    public ResourceKey<Enchantment> getKey(ItemStack stack) {
        for (Map.Entry<ResourceKey<Enchantment>, BasicIngredient> entry : register.entrySet()) {
            if (entry.getValue().test(stack)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getName() {
        return this == CLIENT ? "client" : "server";
    }

    public Pair<List<String>, List<String>> encode() {
        List<String> items = new ArrayList<>(register.size()), enchants = new ArrayList<>(register.size());
        register.forEach((enchant, item) -> {
            enchants.add(enchant.location().toString());
            items.add(item.encodeAsString());
        });
        return Pair.of(enchants, items);
    }

    public void expandTags() {
        Set<Pair<ResourceKey<Enchantment>, BasicIngredient>> updatedTags = new HashSet<>();
        for (var iterator = register.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<ResourceKey<Enchantment>, BasicIngredient> entry = iterator.next();
            BasicIngredient ingredient = entry.getValue();
            if (ingredient.isTag()) {
                Optional<HolderSet.Named<Item>> tags = BuiltInRegistries.ITEM.getTag(ingredient.tag());
                if (tags.isEmpty() || tags.orElseThrow().size() == 0) {
                    DoltasticEnchantments.LOGGER.info("Associated empty tag {} as reagent of enchantment {}, this is an error in your scripts", ingredient.tag().location(), entry.getKey());
                } else {
                    iterator.remove();
                    updatedTags.add(Pair.of(entry.getKey(), new BasicIngredient(tags.orElseThrow().stream().map(a ->
                            EnchantCostUtil.basicCost(a.unwrapKey().orElseThrow().location().toString(), BasicIngredient.ENCHANT_COST)).toList())));
                }
            }
        }
        updatedTags.forEach(pair -> this.put(pair.getFirst(), pair.getSecond()));
    }


}

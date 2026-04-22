package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("removal")
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

    public boolean containsValue(ItemStack stack, RegistryAccess access) {
        return register.values().stream().anyMatch(ing -> ing.test(stack, access));
    }

    public Map<ResourceKey<Enchantment>, BasicIngredient> getRegister() {
        return register;
    }

    public ResourceKey<Enchantment> getKey(ItemStack stack, RegistryAccess access) {
        for (Map.Entry<ResourceKey<Enchantment>, BasicIngredient> entry : register.entrySet()) {
            if (entry.getValue().test(stack, access)) {
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
}

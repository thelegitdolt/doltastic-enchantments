package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

    private final Map<ResourceKey<Enchantment>, ResourceKey<Item>> register = new HashMap<>();

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

    public ResourceKey<Item> get(ResourceKey<Enchantment> enchantment) {
        return register.get(enchantment);
    }

    public ResourceKey<Item> put(ResourceKey<Enchantment> enchantment, ResourceKey<Item> item) {
        return register.put(enchantment, item);
    }

    public ResourceKey<Item> put(ResourceLocation enchantment, ResourceLocation item) {
        return put(ResourceKeyUtil.enchant(enchantment), ResourceKeyUtil.item(item));
    }

    public ResourceKey<Item> put(String enchantment, String item) {
        return put(new ResourceLocation(enchantment), new ResourceLocation(item));
    }

    public boolean containsKey(ResourceKey<Enchantment> enchantment) {
        return register.containsKey(enchantment);
    }

    public boolean containsValue(ResourceKey<Item> item) {
        return register.containsValue(item);
    }

    public Map<ResourceKey<Enchantment>, ResourceKey<Item>> getRegister() {
        return register;
    }

    public ResourceKey<Enchantment> getKey(ResourceKey<Item> itemKey) {
        for (Map.Entry<ResourceKey<Enchantment>, ResourceKey<Item>> entry : register.entrySet()) {
            if (entry.getValue().equals(itemKey)) {
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
            items.add(item.location().toString());
        });
        return Pair.of(enchants, items);
    }
}

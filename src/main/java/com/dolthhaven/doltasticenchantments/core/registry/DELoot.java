package com.dolthhaven.doltasticenchantments.core.registry;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DELoot {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister
            .create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DoltasticEnchantments.MOD_ID);

//    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ANCIENT_BOOK = LOOT_MODIFIERS.register("ancient_book", DoltasticBookLootModifier.CODEC);
}

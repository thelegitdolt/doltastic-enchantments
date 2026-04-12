package com.dolthhaven.doltasticenchantments.core.registry;

import com.dolthhaven.doltasticenchantments.common.loot.DoltasticBookLootModifier;
import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DELoot {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister
            .create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DoltasticEnchantments.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ANCIENT_BOOK = LOOT_MODIFIERS.register("ancient_book", DoltasticBookLootModifier.CODEC);
}

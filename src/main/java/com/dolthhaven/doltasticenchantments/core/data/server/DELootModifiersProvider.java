package com.dolthhaven.doltasticenchantments.core.data.server;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.registry.DEItems;
import com.teamabnormals.blueprint.common.loot.modification.LootModifierProvider;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootPoolEntriesModifier;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootPoolsModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;

public class DELootModifiersProvider extends LootModifierProvider {
    public DELootModifiersProvider(GatherDataEvent e) {
        super(DoltasticEnchantments.MOD_ID, e.getGenerator().getPackOutput(), e.getLookupProvider());
    }

    @Override
    protected void registerEntries(HolderLookup.Provider provider) {
        this.entry("fairy_dust_stuff").selects(BuiltInLootTables.SIMPLE_DUNGEON, BuiltInLootTables.ABANDONED_MINESHAFT, BuiltInLootTables.DESERT_PYRAMID, BuiltInLootTables.JUNGLE_TEMPLE, BuiltInLootTables.ANCIENT_CITY, BuiltInLootTables.NETHER_BRIDGE)
                .addModifier(pools(pool("fairy_dust").setRolls(ConstantValue.exactly(1))
                        .add(EmptyLootItem.emptyItem().setWeight(4)).add(bentry(DEItems.FAIRY_DUST.get(), 1, 2, 6)).build()));
    }

    private static LootPoolEntriesModifier entries(int index, LootPoolEntryContainer... entries) {
        return new LootPoolEntriesModifier(false, index, entries);
    }

    private static LootPoolsModifier pools(LootPool... pools) {
        return new LootPoolsModifier(List.of(pools), false);
    }

    private static LootPool.Builder pool(String name) {
        return LootPool.lootPool().name(DoltasticEnchantments.MOD_ID + ":" + name);
    }

    private static LootPoolEntryContainer.Builder<?> bentry(ItemLike item, int weight, int min, int max) {
        return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
    }

    private static LootPoolEntryContainer entry(ItemLike item, int weight, int min, int max) {
        return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))).build();
    }
}

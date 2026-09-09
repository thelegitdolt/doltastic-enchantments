package com.dolthhaven.doltasticenchantments.core.data.server;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.teamabnormals.blueprint.common.remolder.Remolder;
import com.teamabnormals.blueprint.common.remolder.data.RemolderProvider;
import com.teamabnormals.blueprint.common.remolder.util.LootRemolders;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.MultiResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DELootRemolder extends RemolderProvider {
    public DELootRemolder(GatherDataEvent event) {
        super(DoltasticEnchantments.MOD_ID, PackOutput.Target.DATA_PACK, event.getGenerator().getPackOutput(), event.getLookupProvider());
    }

    @Override
    protected void registerEntries(HolderLookup.Provider provider) {
        this.entry("add_fairy_dust_uncommon")
            .path(new MultiResourceSelector(
                    pickTable(BuiltInLootTables.ANCIENT_CITY),
                    pickTable(BuiltInLootTables.END_CITY_TREASURE),
                    pickTable(BuiltInLootTables.JUNGLE_TEMPLE),
                    pickTable(BuiltInLootTables.DESERT_PYRAMID)
            )).remolder(dustPool(-1f, 1, 3));

        this.entry("add_fairy_dust_common")
            .path(new MultiResourceSelector(
                    pickTable(BuiltInLootTables.SIMPLE_DUNGEON),
                    pickTable(BuiltInLootTables.ABANDONED_MINESHAFT)
            )).remolder(dustPool(-0.2f, 2, 4));

        this.entry("add_fairy_dust_very_common")
            .path(pickTable(BuiltInLootTables.STRONGHOLD_LIBRARY))
            .remolder(dustPool(0.5f, 2, 10));
}

    public static NamesResourceSelector pickTable(ResourceKey<?> key) {
        return new NamesResourceSelector(key.location().withPath("loot_table/"));
    }

    public static Remolder dustPool(float chance, int minCount, int maxCount) {
        return LootRemolders.addPool(LootPool.lootPool().name("dust")
                .setRolls(UniformGenerator.between(chance, 1f))
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount))).build());
    }
}

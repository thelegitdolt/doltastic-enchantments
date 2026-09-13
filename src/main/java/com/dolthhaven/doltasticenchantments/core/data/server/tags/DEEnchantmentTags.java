package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static net.minecraft.world.item.enchantment.Enchantments.*;

public class DEEnchantmentTags extends EnchantmentTagsProvider {
    public DEEnchantmentTags(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), DoltasticEnchantments.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DETags.Enchantments.TREASURE).add(SOUL_SPEED, SWIFT_SNEAK, MENDING)
                .addOptional(ResourceLocation.parse("airhop:air_hop"))
                .addOptional(ResourceLocation.parse("supplementaries:stasis"))
                .addOptional(ResourceLocation.parse("netherexp:phantasm_hull"));

        tag(DETags.Enchantments.DOESNT_REQUIRE_BOOKS).add(EFFICIENCY, IMPALING, LURE, POWER, PROTECTION, SILK_TOUCH, SWEEPING_EDGE);
    }
}

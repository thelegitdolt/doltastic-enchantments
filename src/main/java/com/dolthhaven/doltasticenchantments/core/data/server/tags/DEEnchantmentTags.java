package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DEEnchantmentTags extends EnchantmentTagsProvider {
    public DEEnchantmentTags(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), DoltasticEnchantments.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DETags.Enchantments.TREASURE).add(Enchantments.SOUL_SPEED, Enchantments.SWIFT_SNEAK, Enchantments.MENDING)
                .addOptional(ResourceLocation.parse("airhop:air_hop"))
                .addOptional(ResourceLocation.parse("supplementaries:stasis"))
                .addOptional(ResourceLocation.parse("netherexp:phantasm_hull"));
    }
}

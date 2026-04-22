package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class DEEnchantmentTags extends IntrinsicHolderTagsProvider<Enchantment> {
    public DEEnchantmentTags(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), Registries.ENCHANTMENT, event.getLookupProvider(), enchantment -> ForgeRegistries.ENCHANTMENTS.getResourceKey(enchantment).get(), DoltasticEnchantments.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DETags.Enchantments.TREASURE).add(Enchantments.SOUL_SPEED, Enchantments.SWIFT_SNEAK, Enchantments.MENDING);
    }
}

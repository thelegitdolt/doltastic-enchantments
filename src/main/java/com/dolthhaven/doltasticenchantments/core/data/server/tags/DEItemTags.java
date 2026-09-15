package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DEItemTags extends ItemTagsProvider {
    public DEItemTags(GatherDataEvent event, CompletableFuture<TagLookup<Block>> blockTags) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), blockTags , DoltasticEnchantments.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DETags.Items.CONJURE_BASES).add(Items.BOOK);
    }
}

package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DEBlockTags extends BlockTagsProvider {
    public DEBlockTags(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), DoltasticEnchantments.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}

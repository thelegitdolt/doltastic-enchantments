package com.dolthhaven.doltasticenchantments.core;

import com.teamabnormals.blueprint.core.data.client.BlueprintItemModelProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import static com.dolthhaven.doltasticenchantments.core.DEItems.FAIRY_DUST;

public class DEItemsModelsGen extends BlueprintItemModelProvider {
    public DEItemsModelsGen(GatherDataEvent e) {
        super(e.getGenerator().getPackOutput(), DoltasticEnchantments.MOD_ID, e.getExistingFileHelper());
    }

    @Override
    protected void registerModels() {
        generatedItem(FAIRY_DUST);
    }
}

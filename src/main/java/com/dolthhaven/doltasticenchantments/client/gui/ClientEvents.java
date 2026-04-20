package com.dolthhaven.doltasticenchantments.client.gui;

import me.alfie.immersiveenchanting.api.TooltipDescriptionExtensions;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ClientEvents {
    public static void registerInternalEnchantingTooltips(FMLLoadCompleteEvent event) {
        TooltipDescriptionExtensions.register(new AncientTooltipExtension());
    }
}

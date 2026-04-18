package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantmentReagentSyncPacket;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.alfie.immersiveenchanting.ImmersiveEnchanting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
public class EnchantReagentDatapack extends SimpleJsonResourceReloadListener {
    private MinecraftServer server;
    private static final String DIRECTORY = "reagent";
    public static final EnchantReagentDatapack DATAPACK = new EnchantReagentDatapack(DIRECTORY);

    public EnchantReagentDatapack(String pDirectory) {
        super(new Gson(), pDirectory);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pathedJsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ReagentsRegistry registry = ReagentsRegistry.server();
        registry.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : pathedJsons.entrySet()) {
            for (Map.Entry<String, JsonElement> map : entry.getValue().getAsJsonObject().asMap().entrySet()) {
                String enchant = map.getKey();
                String item = map.getValue().getAsString();
                DoltasticEnchantments.LOGGER.info("Associating {} enchantment with reagent {}", enchant, item);
                registry.put(enchant, item);
            }
        }

        if (this.server != null) {
            int count = 0;

            for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
                EnchantmentReagentSyncPacket.sync(player);
                ++count;
            }

            ImmersiveEnchanting.LOGGER.info("Synced server enchantment reagent registry with " + count + " client(s).");
        }

    }
}

package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantReagentSyncPacket;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.dolthhaven.doltasticenchantments.integration.DEReliableRemoverCompat;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@ParametersAreNonnullByDefault
public class EnchantReagentDatapack extends SimpleJsonResourceReloadListener {
    private MinecraftServer server;
    private Registry<Enchantment> enchantReg;
    private static final String DIRECTORY = "reagent";
    public static final EnchantReagentDatapack DATAPACK = new EnchantReagentDatapack(DIRECTORY);

    public EnchantReagentDatapack(String directory) {
        super(new Gson(), directory);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void setRegistry(RegistryAccess access) {
        enchantReg = access.registryOrThrow(Registries.ENCHANTMENT);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pathedJsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ReagentsRegistry reagentsReg = new ReagentsRegistry();
        reagentsReg.clear();

        int reagentCount = 0;
        DoltasticEnchantments.LOGGER.info("Loaded {} reagent jsons with paths as follows: {}", pathedJsons.size(),
                EnchantCostUtil.reduceToString(pathedJsons.keySet(), Function.identity(), ", "));

        for (Map.Entry<ResourceLocation, JsonElement> jsonFile : pathedJsons.entrySet()) {
            ResourceLocation path = jsonFile.getKey();
            for (Map.Entry<String, JsonElement> jsonEntry : jsonFile.getValue().getAsJsonObject().asMap().entrySet()) {
                Holder<Enchantment> enchant = getEnchantmentOrError(jsonEntry.getKey(), path);
                if (enchant == null) continue;

                CostHolder cost = BasicIngredient.parseJsonAndError(jsonEntry.getValue(), path);
                if (cost == null) continue;

                boolean shouldPutNew = calculatePriority(reagentsReg, enchant, cost);

                if (shouldPutNew) {
                    if (!reagentsReg.containsKey(enchant)) reagentCount++;
                    reagentsReg.put(enchant, cost);
                }
            }
        }
        DoltasticEnchantments.LOGGER.info("Successfully loaded reagents for {} enchantments", reagentCount);


        syncWithServer();
        logUnreagentedEnchants(this.access);
    }

    private Holder<Enchantment> getEnchantmentOrError(String str, ResourceLocation path) {
        ResourceLocation loc = ResourceLocation.tryParse(str);
        if (loc != null) {
            Holder<Enchantment> enchantment = enchantReg.getHolder(loc).orElse(null);
            if (enchantment != null) return enchantment;
        }

        if (str.contains("comment")) {
            DoltasticEnchantments.LOGGER.error("Datapack {} contains unregistered enchantment {}", path, str);
        }
        return null;
    }

    private void syncWithServer() {
        if (this.server != null) {
            int count = 0;

            for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
                EnchantReagentSyncPacket.sync(player);
                ++count;
            }

            DoltasticEnchantments.LOGGER.info("Synced server enchantment reagent registry with {} client(s).", count);
        }
    }

    public static void logUnreagentedEnchants(Registry<Enchantment> reg) {
        List<Holder<Enchantment>> missingList = new ArrayList<>(), booklessList = new ArrayList<>();
        reg.holders()
            .filter(enchantment -> !DEReliableRemoverCompat.isEnchantmentRemoved(enchantment))
            .forEach(enchantment -> {
                if (!ReagentsRegistry.server().containsKey(enchantment)) {
                    boolean requiresBook = !ResourceUtil.isTag(enchantment, DETags.Enchantments.DOESNT_REQUIRE_BOOKS, reg);
                    boolean notTreasureEnchant = !ResourceUtil.isTag(enchantment, DETags.Enchantments.TREASURE, reg);
                    (requiresBook && notTreasureEnchant ?  missingList : booklessList).add(enchantment);
                }
            });

        if (!missingList.isEmpty())
            DoltasticEnchantments.LOGGER.warn("The following enchantments have no associated reagent: {}", EnchantCostUtil.reduceToString(missingList, ResourceKey::location,  ", "));
        if (!booklessList.isEmpty())
            DoltasticEnchantments.LOGGER.info("The following enchantments have no associated reagent, but this is fine because these are treasure or are bookless: {}", EnchantCostUtil.reduceToString(booklessList, ResourceKey::location, ", "));
    }

    // if there are duplicate entries for a single enchantment, whether to replace an old entry with a new entry
    // prioritizes an entry with modded IDs, otherwise overwrites
    private boolean calculatePriority(ReagentsRegistry reagentReg, Holder<Enchantment> enchant, CostHolder ingredient) {
        if (!reagentReg.containsKey(enchant)) return true;

        CostHolder oldIng = reagentReg.get(enchant);
        boolean newIsModded = BasicIngredient.hasModdedIds(ingredient);
        boolean oldIsModded = BasicIngredient.hasModdedIds(oldIng);
        // prioritize whichever entry has modded ids
        if (newIsModded && !oldIsModded) {
            return true;
        } else return oldIsModded && !newIsModded;
    }
}

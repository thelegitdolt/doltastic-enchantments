package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.CostHolderUtils;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.dolthhaven.doltasticenchantments.integration.DEReliableRemoverCompat;
import com.google.gson.JsonElement;
import me.alfie.alfinolib.datapacks.DatapackDefinition;
import me.alfie.alfinolib.datapacks.DatapackKey;
import me.alfie.alfinolib.datapacks.ModDatapack;
import me.alfie.immersiveenchanting.datapack.enchantment_cost.codec.CostHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@ParametersAreNonnullByDefault
public class ReagentDatapack extends ModDatapack<CostHolder, ReagentsRegistry> {
    public static final DatapackKey<ReagentsRegistry> KEY = new DatapackKey<>(DoltasticEnchantments.MOD_ID, "reagent");
    public static final DatapackDefinition<ReagentsRegistry> DEFINITION = new DatapackDefinition<>(KEY, ReagentsRegistry.STREAM_CODEC);

    private MinecraftServer server;
    private final Registry<Enchantment> enchantReg;

    public ReagentDatapack(RegistryAccess access) {
        super(CostHolder.CODEC, DEFINITION, access);
        enchantReg = access.registryOrThrow(Registries.ENCHANTMENT);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public ReagentsRegistry getData() {
        return null;
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

                CostHolder cost = CostHolderUtils.parseJsonAndError(jsonEntry.getValue(), jsonEntry.getKey(), path);
                if (cost == null) continue;

                boolean shouldPutNew = calculatePriority(reagentsReg, enchant, cost);

                if (shouldPutNew) {
                    if (!reagentsReg.containsKey(enchant)) reagentCount++;
                    reagentsReg.put(enchant, cost);
                }
            }
        }
        DoltasticEnchantments.LOGGER.info("Successfully loaded reagents for {} enchantments", reagentCount);


//        syncWithServer();
        logUnreagentedEnchants(this.enchantReg);
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

//    private void syncWithServer() {
//        if (this.server != null) {
//            int count = 0;
//
//            for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
//                EnchantReagentSyncPacket.sync(player);
//                ++count;
//            }
//
//            DoltasticEnchantments.LOGGER.info("Synced server enchantment reagent registry with {} client(s).", count);
//        }
//    }

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
            DoltasticEnchantments.LOGGER.warn("The following enchantments have no associated reagent: {}", EnchantCostUtil.reduceToString(missingList, holder -> holder.unwrapKey().orElseThrow().location(),  ", "));
        if (!booklessList.isEmpty())
            DoltasticEnchantments.LOGGER.info("The following enchantments have no associated reagent, but this is fine because these are treasure or are bookless: {}", EnchantCostUtil.reduceToString(booklessList, holder -> holder.unwrapKey().orElseThrow().location(), ", "));
    }

    // if there are duplicate entries for a single enchantment, whether to replace an old entry with a new entry
    // prioritizes an entry with modded IDs, otherwise overwrites
    private boolean calculatePriority(ReagentsRegistry reagentReg, Holder<Enchantment> enchant, CostHolder ingredient) {
        if (!reagentReg.containsKey(enchant)) return true;

        CostHolder oldIng = reagentReg.get(enchant);
        boolean newIsModded = CostHolderUtils.hasModdedIds(ingredient);
        boolean oldIsModded = CostHolderUtils.hasModdedIds(oldIng);
        // prioritize whichever entry has modded ids
        if (newIsModded && !oldIsModded) {
            return true;
        } else return oldIsModded && !newIsModded;
    }
}

package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
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

    private ReagentsRegistry Data = null;
    private final RegistryAccess access;

    public ReagentDatapack(RegistryAccess access) {
        super(CostHolder.CODEC, DEFINITION, access);
        this.access = access;
    }

    @Override
    public ReagentsRegistry getData() {
        return Data;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pathedJsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ReagentsRegistry reagentsReg = new ReagentsRegistry();
        this.Data = reagentsReg;

        int reagentCount = 0;
        DoltasticEnchantments.LOGGER.info("Loaded {} reagent jsons with paths as follows: {}", pathedJsons.size(),
                EnchantCostUtil.reduceToString(pathedJsons.keySet(), Function.identity(), ", "));

        for (Map.Entry<ResourceLocation, JsonElement> jsonFile : pathedJsons.entrySet()) {
            ResourceLocation path = jsonFile.getKey();
            for (Map.Entry<String, JsonElement> jsonEntry : jsonFile.getValue().getAsJsonObject().asMap().entrySet()) {
                Holder<Enchantment> enchant = getEnchantmentOrError(jsonEntry.getKey(), path);
                if (enchant == null) continue;

                CostHolder cost = EnchantCostUtil.parseJsonAndError(jsonEntry.getValue(), jsonEntry.getKey(), path);
                if (cost == null) continue;

                boolean shouldPutNew = calculatePriority(reagentsReg, enchant, cost);

                if (shouldPutNew) {
                    if (!reagentsReg.containsKey(enchant)) reagentCount++;
                    reagentsReg.put(enchant, cost);
                }
            }
        }
        DoltasticEnchantments.LOGGER.info("Successfully loaded reagents for {} enchantments", reagentCount);

        logUnreagentedEnchants(reagentsReg);
    }

    private Holder<Enchantment> getEnchantmentOrError(String str, ResourceLocation path) {
        ResourceLocation loc = ResourceLocation.tryParse(str);
        if (loc != null) {
            Holder<Enchantment> enchantment = reg().getHolder(ResourceUtil.enchant(loc)).orElse(null);
            if (enchantment != null) return enchantment;
        }

        if (!str.contains("comment")) {
            DoltasticEnchantments.LOGGER.error("Datapack {} contains unregistered enchantment {}", path, str);
        }
        return null;
    }

    public void logUnreagentedEnchants(ReagentsRegistry registry) {
        List<Holder<Enchantment>> missingList = new ArrayList<>(), booklessList = new ArrayList<>();
        reg().holders()
            .filter(enchantment -> !DEReliableRemoverCompat.isEnchantmentRemoved(enchantment))
            .forEach(enchantment -> {
                if (!registry.containsKey(enchantment)) {
                    boolean requiresBook = !ResourceUtil.isTag(enchantment, DETags.Enchantments.DOESNT_REQUIRE_BOOKS, reg());
                    boolean notTreasureEnchant = !ResourceUtil.isTag(enchantment, DETags.Enchantments.TREASURE, reg());
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
        boolean newIsModded = EnchantCostUtil.hasModdedIds(ingredient);
        boolean oldIsModded = EnchantCostUtil.hasModdedIds(oldIng);
        boolean oldEmpty = EnchantCostUtil.isEmpty(oldIng);
        boolean newEmpty = EnchantCostUtil.isEmpty(ingredient);
        if (newEmpty) return false;
        if (oldEmpty) return true;

        // prioritize whichever entry has modded ids
        if (newIsModded && !oldIsModded) {
            return true;
        } else return oldIsModded && !newIsModded;
    }

    private Registry<Enchantment> reg() {
        return access.registryOrThrow(Registries.ENCHANTMENT);
    }
}

package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantReagentSyncPacket;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceKeyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.alfie.immersiveenchanting.datapack.EnchantmentCostRegistry;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
public class EnchantReagentDatapack extends SimpleJsonResourceReloadListener {
    private MinecraftServer server;
    private RegistryAccess access = null;
    private static final String DIRECTORY = "reagent";
    public static final EnchantReagentDatapack DATAPACK = new EnchantReagentDatapack(DIRECTORY);

    public EnchantReagentDatapack(String pDirectory) {
        super(new Gson(), pDirectory);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void setAccess(RegistryAccess access) {
        this.access = access;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pathedJsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        if (access == null) throw new IllegalStateException("Started parsing enchantments without registry access");

        ReagentsRegistry reagentsReg = ReagentsRegistry.server();
        Registry<Item> itemReg = access.registry(Registries.ITEM).orElse(null);
        Registry<Enchantment> enchantReg = access.registry(Registries.ENCHANTMENT).orElse(null);

        if (itemReg == null || enchantReg == null) {
            DoltasticEnchantments.LOGGER.error("Cannot load enchantment reagents; registries are missing");
            return;
        }

        reagentsReg.clear();
        int reagentCount = 0;
        for (Map.Entry<ResourceLocation, JsonElement> jsonFile : pathedJsons.entrySet()) {
            for (Map.Entry<String, JsonElement> jsonEntry : jsonFile.getValue().getAsJsonObject().asMap().entrySet()) {
                ResourceLocation enchant = new ResourceLocation(jsonEntry.getKey());
                BasicIngredient ingredient = BasicIngredient.parseJson(jsonEntry.getValue());

                boolean shouldPutNew = validateIDs(enchant, ingredient, jsonFile.getKey(), itemReg, enchantReg)
                        && calculatePriority(reagentsReg, ResourceKeyUtil.enchant(enchant), ingredient);

                if (shouldPutNew) {
                    reagentCount++;
                    reagentsReg.put(enchant, ingredient);
                }
            }
        }
        DoltasticEnchantments.LOGGER.info("Successfully loaded {} reagents", reagentCount);


        syncWithServer();
        logUnreagentedEnchants(this.access);
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

    public static void logUnreagentedEnchants(RegistryAccess access) {
        List<ResourceKey<Enchantment>> missingList = new ArrayList<>(), booklessList = new ArrayList<>();
        access.registry(Registries.ENCHANTMENT).ifPresentOrElse(reg -> reg
            .stream().forEach(enchantment -> {
                ResourceKey<Enchantment> enchantKey = reg.getResourceKey(enchantment).orElseThrow();
                if (!ReagentsRegistry.server().containsKey(enchantKey)) {
                    boolean isRequireBook = EnchantCostUtil.requiresBook(EnchantmentCostRegistry.getServerRegistry().getEnchantmentCost(enchantKey));
                    (isRequireBook ? missingList : booklessList).add(enchantKey);
                }
            }), () -> DoltasticEnchantments.LOGGER.warn("Could not find registry; this is strange"));

        if (!missingList.isEmpty())
            DoltasticEnchantments.LOGGER.warn("The following enchantments have no associated reagent: {}", EnchantCostUtil.keyListToString(missingList, ", "));
        if (!booklessList.isEmpty())
            DoltasticEnchantments.LOGGER.info("The following enchantments have no associated reagent, but this is fine because these don't require books: {}", EnchantCostUtil.keyListToString(booklessList, ", "));
    }



    private boolean validateIDs(ResourceLocation enchantKey, BasicIngredient ingredient, ResourceLocation path, Registry<Item> itemReg, Registry<Enchantment> enchantReg) {
        boolean valid = true;
        if (!enchantReg.containsKey(enchantKey)) {
            DoltasticEnchantments.LOGGER.warn("Unknown enchantment {} read in mapping at path {}", enchantKey, path);
            valid = false;
        }
        if (!ingredient.isValid(itemReg, enchantKey, path)) {
            valid = false;
        }
        return valid;
    }

    private boolean calculatePriority(ReagentsRegistry reagentReg, ResourceKey<Enchantment> enchantKey, BasicIngredient ingredient) {
        if (!reagentReg.containsKey(enchantKey)) return true;

        BasicIngredient oldItem = reagentReg.get(enchantKey);
        if (ingredient.hasModdedIds() && !oldItem.hasModdedIds()) {
            reagentReg.getRegister().remove(enchantKey);
            return true;
        } else return ingredient.hasModdedIds() || !oldItem.hasModdedIds();
    }
}

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
public class EnchantReagentDatapack extends SimpleJsonResourceReloadListener {
    private MinecraftServer server;
    private RegistryAccess access;
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
        ReagentsRegistry reagentsReg = ReagentsRegistry.server();
        Registry<Item> itemReg = access.registry(Registries.ITEM).orElse(null);
        Registry<Enchantment> enchantReg = access.registry(Registries.ENCHANTMENT).orElse(null);

        if (itemReg == null || enchantReg == null) {
            DoltasticEnchantments.LOGGER.error("Cannot load enchantment reagents; registries are missing");
            return;
        }

        reagentsReg.clear();
        for (Map.Entry<ResourceLocation, JsonElement> jsonFile : pathedJsons.entrySet()) {
            for (Map.Entry<String, JsonElement> jsonEntry : jsonFile.getValue().getAsJsonObject().asMap().entrySet()) {
                ResourceLocation enchant = new ResourceLocation(jsonEntry.getKey()), item = new ResourceLocation(jsonEntry.getValue().getAsString());

                boolean shouldPutNew = validateIDs(enchant, item, jsonFile.getKey(), itemReg, enchantReg)
                        && calculatePriority(reagentsReg, ResourceKeyUtil.enchant(enchant), ResourceKeyUtil.item(item));

                if (shouldPutNew) {
                    DoltasticEnchantments.LOGGER.info("Associating {} enchantment with reagent {}", enchant, item);
                    reagentsReg.put(enchant, item);
                }
            }
        }

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
            DoltasticEnchantments.LOGGER.warn("The following enchantments have no associated reagent: {}", listToString(missingList));
        if (!booklessList.isEmpty())
            DoltasticEnchantments.LOGGER.info("The following enchantments have no associated reagent, but this is fine because these don't require books: {}", listToString(booklessList));
    }

    private static String listToString(List<ResourceKey<Enchantment>> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Iterator<ResourceKey<Enchantment>> iterator = list.iterator(); iterator.hasNext();) {
            stringBuilder.append(iterator.next().location());
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private boolean validateIDs(ResourceLocation enchantKey, ResourceLocation itemKey, ResourceLocation path, Registry<Item> itemReg, Registry<Enchantment> enchantReg) {
        boolean valid = true;
        if (!enchantReg.containsKey(enchantKey)) {
            DoltasticEnchantments.LOGGER.warn("Unknown enchantment {} read in mapping at path {}", enchantKey, path);
            valid = false;
        }
        if (!itemReg.containsKey(itemKey)) {
            valid = false;
            DoltasticEnchantments.LOGGER.warn("Unknown item {} read in mapping at path {}", itemKey, path);
        }
        return valid;
    }

    private boolean calculatePriority(ReagentsRegistry reagentReg, ResourceKey<Enchantment> enchantKey, ResourceKey<Item> itemKey) {
        Predicate<ResourceLocation> isDefaultNamespace = item -> item.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE);
        ResourceLocation itemLoc = itemKey.location();

        if (!reagentReg.containsKey(enchantKey)) return true;

        ResourceLocation oldItem = reagentReg.get(enchantKey).location();
        if (isDefaultNamespace.test(oldItem) && !isDefaultNamespace.test(itemLoc)) {
            reagentReg.getRegister().remove(enchantKey);
            DoltasticEnchantments.LOGGER.info("Enchantment {} already has a vanilla reagent {} but new reagent {} is modded, overriding", enchantKey, oldItem, itemKey);
            return true;
        } else if (isDefaultNamespace.test(oldItem) && isDefaultNamespace.test(itemLoc)) {
            DoltasticEnchantments.LOGGER.info("Enchantment {} has a non-vanilla reagent {} but new reagent {} is vanilla, not overriding", enchantKey, oldItem, itemKey);
            return false;
        } else {
            DoltasticEnchantments.LOGGER.info("Found two different reagent entries {} and {} for enchantment {}, keeping {}", oldItem, itemKey, enchantKey, itemKey);
            return true;
        }
    }
}

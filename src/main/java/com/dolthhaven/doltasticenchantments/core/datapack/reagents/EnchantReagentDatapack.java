package com.dolthhaven.doltasticenchantments.core.datapack.reagents;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.dolthhaven.doltasticenchantments.core.networking.EnchantReagentSyncPacket;
import com.dolthhaven.doltasticenchantments.core.utils.EnchantCostUtil;
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
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

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
        ReagentsRegistry registry = ReagentsRegistry.server();
        registry.clear();
        for (Map.Entry<ResourceLocation, JsonElement> jsonFile : pathedJsons.entrySet()) {
            for (Map.Entry<String, JsonElement> jsonEntry : jsonFile.getValue().getAsJsonObject().asMap().entrySet()) {
                ResourceLocation enchant = new ResourceLocation(jsonEntry.getKey()), item = new ResourceLocation(jsonEntry.getValue().getAsString());
                if (validate(enchant, item, jsonFile.getKey())) {
                    DoltasticEnchantments.LOGGER.info("Associating {} enchantment with reagent {}", enchant, item);
                    registry.put(enchant, item);
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
                if (!ReagentsRegistry.server().contains(enchantKey)) {
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

    private boolean validate(ResourceLocation enchantKey, ResourceLocation itemKey, ResourceLocation path) {
        Optional<Registry<Enchantment>> enchantRegMaybe = access.registry(Registries.ENCHANTMENT);
        Optional<Registry<Item>> itemRegMaybe  = access.registry(Registries.ITEM);
        MutableBoolean valid = new MutableBoolean(true);
        enchantRegMaybe.ifPresent(enchantReg -> {
            itemRegMaybe.ifPresent(itemReg -> {
                if (!enchantReg.containsKey(enchantKey)) {
                    DoltasticEnchantments.LOGGER.warn("Unknown enchantment {} read in mapping at path {}", enchantKey, path);
                    valid.setFalse();
                }
                if (!itemReg.containsKey(itemKey)) {
                    valid.setFalse();
                    DoltasticEnchantments.LOGGER.warn("Unknown item {} read in mapping at path {}", itemKey, path);
                }
            });
        });
        return valid.booleanValue();
    }
}

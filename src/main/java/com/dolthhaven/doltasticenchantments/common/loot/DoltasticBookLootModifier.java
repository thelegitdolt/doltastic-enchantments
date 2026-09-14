package com.dolthhaven.doltasticenchantments.common.loot;

import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.utils.ResourceUtil;
import com.dolthhaven.doltasticenchantments.integration.DEReliableRemoverCompat;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// todo: test if removing the conditions thing works
public class DoltasticBookLootModifier extends LootModifier {
    public static final Supplier<MapCodec<DoltasticBookLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance ->
            instance.group(LOOT_CONDITIONS_CODEC.lenientOptionalFieldOf("conditions", new LootItemCondition[0]).forGetter((lm) -> lm.conditions))
                    .and(BookInstance.CODEC.listOf().fieldOf("injections").forGetter(book -> book.booksToInject))
                    .apply(instance, DoltasticBookLootModifier::new)));

    private final List<BookInstance> booksToInject;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected DoltasticBookLootModifier(LootItemCondition[] conditionsIn, List<BookInstance> booksToInject) {
        super(conditionsIn);
        this.booksToInject = booksToInject;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (BookInstance instance : booksToInject) {
            RandomSource random = context.getRandom();

            if (!instance.tables.contains(context.getQueriedLootTableId())) continue;
            for (int i = 0; i < getBookCount(instance.weight, context.getRandom()); i++) {
                List<Holder<Enchantment>> enchantPool = instance.getEnchantments(context.getLevel());
                List<Holder<Enchantment>> toApply = sample(instance
                        .getEnchantments(context.getLevel()), instance.sampleCount.sample(random), random);;

                if (enchantPool.isEmpty()) continue;
                ItemStack book = BookUtil.newBookWith(toApply);

                generatedLoot.add(book);
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public record BookInstance(HolderSet<Enchantment> enchantments, HolderSet<Item> items, HolderSet<Item> butNot, boolean commonEnchants, List<ResourceLocation> tables, float weight, UniformInt sampleCount, List<Holder<Enchantment>> derivedPool) {
        private static final Supplier<List<Item>> COMMON_ITEMS = Suppliers.memoize(() -> {
            return Util.make(new ArrayList<>(), list -> {
                list.add(Items.DIAMOND_PICKAXE);
                list.add(Items.DIAMOND_AXE);
                list.add(Items.DIAMOND_HOE);
                list.add(Items.DIAMOND_SHOVEL);
                list.add(Items.DIAMOND_SWORD);
                list.add(Items.DIAMOND_HELMET);
                list.add(Items.DIAMOND_CHESTPLATE);
                list.add(Items.DIAMOND_LEGGINGS);
                list.add(Items.DIAMOND_BOOTS);
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("farmersdelight", "diamond_knife"));
                if (item != Items.AIR) list.add(item);
            });
        });

        public static final Codec<BookInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("enchantments", HolderSet.empty()).forGetter(BookInstance::enchantments),
                RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items", HolderSet.empty()).forGetter(BookInstance::items),
                RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("butNot", HolderSet.empty()).forGetter(BookInstance::items),
                Codec.BOOL.optionalFieldOf("commonEnchants", false).forGetter(BookInstance::commonEnchants),
                ResourceLocation.CODEC.listOf().fieldOf("tables").forGetter(BookInstance::tables),
                Codec.FLOAT.fieldOf("weightedBookCount").forGetter(BookInstance::weight),
                UniformInt.CODEC.fieldOf("enchantCount").orElse(UniformInt.of(1, 2)).forGetter(BookInstance::sampleCount)
        ).apply(instance, BookInstance::new));

        public BookInstance(HolderSet<Enchantment> enchantments, HolderSet<Item> items, HolderSet<Item> butNot, boolean commonEnchants, List<ResourceLocation> tables, float weight, UniformInt sampleCount) {
            this(enchantments, items, butNot, commonEnchants, tables, weight, sampleCount, new ArrayList<>());
        }

        public List<Holder<Enchantment>> getEnchantments(Level level) {
            if (this.derivedPool.isEmpty()) {
                List<Holder<Enchantment>> pool = computeEnchantments(level);
                derivedPool.addAll(pool);
            }
            return derivedPool;
        }

        private List<Holder<Enchantment>> computeEnchantments(Level level) {
            RegistryAccess access = level.registryAccess();
            Registry<Enchantment> registry = access.registryOrThrow(Registries.ENCHANTMENT);
            if (items.size() == 0 && !commonEnchants) return enchantments.stream().toList();
            else {
                List<Item> items = new ArrayList<>(this.items.stream().map(Holder::value).toList());
                if (commonEnchants) items.addAll(COMMON_ITEMS.get());
                List<Holder.Reference<Enchantment>> holders = registry.holders()
                        // the actual filter
                        .filter(enchantment ->
                                items.stream().anyMatch(item -> enchantment.value().canEnchant(item.getDefaultInstance())) &&
                                butNot.stream().noneMatch(item -> enchantment.value().canEnchant(new ItemStack(item))))
                        // not treasure or removed by reliable remover
                        .filter(enchantment -> {
                            if (DEReliableRemoverCompat.isEnchantmentRemoved(enchantment)) return false;
                            var enchantReg = registry.getTag(DETags.Enchantments.TREASURE);
                            if (enchantReg.isEmpty()) return true;
                            else return !enchantReg.orElseThrow().contains(enchantment);
                        })
                        // requires book and is enabled
                        .filter(enchantment -> {
                            return !DEReliableRemoverCompat.isEnchantmentRemoved(enchantment) && !ResourceUtil.isTag(enchantment, DETags.Enchantments.DOESNT_REQUIRE_BOOKS, level.registryAccess().registryOrThrow(Registries.ENCHANTMENT));
                        })
                        // unbreaking only shows up if we are doing commonEnchant
                        .filter(enchantment -> !this.commonEnchants || !enchantment.is(DETags.Enchantments.UNIVERSAL_ENCHANTS)).toList();
                List<Holder<Enchantment>> allEnchantments = new ArrayList<>();
                this.enchantments.forEach(allEnchantments::add);
                allEnchantments.addAll(holders);
                return allEnchantments;
            }
        }
    }



    private static <E> List<E> sample(List<E> original, int amount, RandomSource random) {
        List<E> sampled = new ArrayList<>();
        List<E> copy = new ArrayList<>(original.size());
        copy.addAll(original);

        for (int i = 0; i < amount; i++) {
            if (copy.isEmpty()) break;
            sampled.add(copy.remove(random.nextInt(copy.size())));
        }
        return sampled;
    }

    public static int getBookCount(float thing, RandomSource random) {
        if (thing < 1) {
            return random.nextDouble() < thing ? 1 : 0;
        }

        int quotient = (int) (thing / 0.5);
        double remainder = thing % 0.5;

        int times = 0;
        for (int i = 0; i < quotient; i++) {
            if (random.nextDouble() < 0.5) times += 1;
        }
        if (random.nextDouble() < remainder) times += 1;

        return times;
    }
}

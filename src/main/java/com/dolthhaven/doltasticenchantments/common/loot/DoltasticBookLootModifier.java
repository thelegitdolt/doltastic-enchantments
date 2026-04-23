package com.dolthhaven.doltasticenchantments.common.loot;

import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DoltasticBookLootModifier extends LootModifier {
    public static final Supplier<Codec<DoltasticBookLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(instance ->
            codecStart(instance)
                    .and(BookInstance.CODEC.listOf().fieldOf("injections").forGetter(a -> a.booksToInject))
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
            if (!instance.tables.contains(context.getQueriedLootTableId())) continue;
            double booksCount = Math.min(
                    Math.ceil(instance.weight) + 1,
                    Math.round(context.getRandom().nextFloat() * instance.weight * 2)
            );
            for (int i = 0; i < booksCount; i++) {
                RandomSource random = context.getRandom();
                List<Holder<Enchantment>> enchantments = sample(instance
                        .getEnchantments(context.getLevel().registryAccess()), instance.sampleCount.sample(random), random);
                if (enchantments.isEmpty()) continue;
                ItemStack book = BookUtil.newBookWith(enchantments);

                generatedLoot.add(book);
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public record BookInstance(List<Holder<Enchantment>> enchantments, List<Holder<Item>> items, boolean commonEnchants, List<ResourceLocation> tables, float weight, UniformInt sampleCount) {
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
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("farmersdelight", "diamond_knife"));
                if (item != null) list.add(item);
            });
        });

        public static final Codec<BookInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ENCHANTMENT.holderByNameCodec().listOf().optionalFieldOf("enchantments", List.of()).forGetter(BookInstance::enchantments),
                BuiltInRegistries.ITEM.holderByNameCodec().listOf().optionalFieldOf("items", List.of()).forGetter(BookInstance::items),
                Codec.BOOL.optionalFieldOf("commonEnchants", false).forGetter(BookInstance::commonEnchants),
                ResourceLocation.CODEC.listOf().fieldOf("tables").forGetter(BookInstance::tables),
                Codec.FLOAT.fieldOf("weightedBookCount").forGetter(BookInstance::weight),
                UniformInt.CODEC.fieldOf("enchantCount").orElse(UniformInt.of(1, 2)).forGetter(BookInstance::sampleCount)
        ).apply(instance, BookInstance::new));

        public List<Holder<Enchantment>> getEnchantments(RegistryAccess access) {
            Registry<Enchantment> registry = access.registryOrThrow(Registries.ENCHANTMENT);
            if (items.isEmpty() && !commonEnchants) return enchantments;
            else {
                List<Item> items = commonEnchants ? COMMON_ITEMS.get() : this.items.stream().map(Holder::value).toList();
                List<Holder<Enchantment>> holders =  registry.holders()
                        .filter(enchantment -> {
                            var enchantReg = registry.getTag(DETags.Enchantments.TREASURE);
                            if (enchantReg.isEmpty()) return true;
                            else return !enchantReg.orElseThrow().contains(enchantment);
                        })
                        .filter(enchantment -> !enchantment.value().isCurse() &&
                                items.stream().anyMatch(item -> enchantment.value().canEnchant(new ItemStack(item))))
                        .map(a -> (Holder<Enchantment>) a).toList();
                List<Holder<Enchantment>> allEnchantments = new ArrayList<>(this.enchantments);
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
}

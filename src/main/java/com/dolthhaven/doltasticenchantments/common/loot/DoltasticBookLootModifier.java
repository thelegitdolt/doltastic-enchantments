package com.dolthhaven.doltasticenchantments.common.loot;

import com.dolthhaven.doltasticenchantments.core.BookUtil;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
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
            if (instance.tables.contains(context.getQueriedLootTableId())) {
                float weight = instance.weight;
                RandomSource random = context.getRandom();
                List<Holder<Enchantment>> enchantments = sample(instance.enchantments, instance.sampleCount.sample(random), random);
                if (enchantments.isEmpty()) continue;
                ItemStack book = BookUtil.newBookWith(enchantments);

                if (random.nextFloat() < weight) {
                    generatedLoot.add(book);
                }
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public record BookInstance(List<Holder<Enchantment>> enchantments, List<ResourceLocation> tables, float weight, UniformInt sampleCount) {
        public static final Codec<BookInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ENCHANTMENT.holderByNameCodec().listOf().fieldOf("enchantments").forGetter(BookInstance::enchantments),
                ResourceLocation.CODEC.listOf().fieldOf("tables").forGetter(BookInstance::tables),
                Codec.FLOAT.fieldOf("weight").forGetter(BookInstance::weight),
                UniformInt.CODEC.fieldOf("sample_count").orElse(UniformInt.of(1, 3)).forGetter(BookInstance::sampleCount)
        ).apply(instance, BookInstance::new));
    }

    private static <E> List<E> sample(List<E> original, int amount, RandomSource random) {
        List<E> sampled = new ArrayList<>();
        List<E> copy = new ArrayList<>(original.size());
        copy.addAll(original);

        for (int i = 0; i < amount; i++) {
            if (copy.isEmpty()) break;
            sampled.add(copy.remove(random.nextInt(original.size())));
        }
        return sampled;
    }
}

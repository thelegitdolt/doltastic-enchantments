package com.dolthhaven.doltasticenchantments.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import me.alfie.immersiveenchanting.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.client.tooltip.EnchantedBookTooltips;
import org.violetmoon.zeta.client.event.play.ZGatherTooltipComponents;
import org.violetmoon.zeta.util.ItemNBTHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Pseudo
@Mixin(EnchantedBookTooltips.class)
public class EnchantingBookTooltipsMixin {
    @Shadow private static List<ItemStack> getItemsForEnchantment(Enchantment e, boolean onlyForTable) {return null;}

    @WrapOperation(method = "makeTooltip", at = @At(value = "INVOKE", target = "Lorg/violetmoon/zeta/client/event/play/ZGatherTooltipComponents;getItemStack()Lnet/minecraft/world/item/ItemStack;"), remap = false)
    private static ItemStack DoltasticEnchantments$AncientBookAlsoGetEnchantment(ZGatherTooltipComponents instance, Operation<ItemStack> original) {
        ItemStack stack = original.call(instance);
        if (!stack.is(ModItems.ANCIENT_BOOK.get())) return stack;

        List<Either<FormattedText, TooltipComponent>> tooltip = instance.getTooltipElements();
        int tooltipIndex = 0;

        List<EnchantmentInstance> instances = correctlyFindAncientBookEnchantments(stack);
        for (EnchantmentInstance ed : instances) {

            while (tooltipIndex < tooltip.size()) {
                Either<FormattedText, TooltipComponent> elmAt = tooltip.get(tooltipIndex);
                if (elmAt.left().isPresent() && elmAt.left().get() instanceof Component component && injectsAfter(ed, component, instances.size() == 1)) {
                    boolean tableOnly = ItemNBTHelper.getBoolean(stack, "quark:only_show_table_enchantments", false);
                    List<ItemStack> items = getItemsForEnchantment(ed.enchantment, tableOnly);
                    int itemCount = items.size();
                    int lines = (int)Math.ceil((double)itemCount / (double)10.0F);
                    int len = 3 + Math.min(10, itemCount) * 9;
                    tooltip.add(tooltipIndex + 1, Either.right(new EnchantedBookTooltips.EnchantedBookComponent(len, lines * 10, ed.enchantment, tableOnly)));
                    break;
                }

                ++tooltipIndex;
            }
        }
        return stack;
    }

    @Unique
    private static @NotNull boolean injectsAfter(EnchantmentInstance ed, Component component, boolean isMulti) {
        try {
            if (component instanceof MutableComponent mutable) {
                return mutable.getSiblings().get(isMulti ? 1 : 0).getContents() instanceof TranslatableContents trans && trans.getKey().equals(ed.enchantment.getDescriptionId());
            }
        }
        catch (Exception ignored) {}
        return false;
    }

    @Unique
    private static List<EnchantmentInstance> correctlyFindAncientBookEnchantments(ItemStack stack) {
        ListTag listTag = EnchantedBookItem.getEnchantments(stack);
        List<EnchantmentInstance> enchants = new ArrayList<>(listTag.size());

        if (!listTag.isEmpty()) {
            for (Tag tag : listTag) {
                if (tag instanceof CompoundTag compoundTag) {
                    ResourceLocation enchantmentRL = ResourceLocation.tryParse(compoundTag.getString("id"));
                    if (enchantmentRL == null) continue;
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentRL);
                    if (enchantment == null) continue;
                    enchants.add(new EnchantmentInstance(enchantment, 1));
                }
            }
        }
        return enchants;
    }
}



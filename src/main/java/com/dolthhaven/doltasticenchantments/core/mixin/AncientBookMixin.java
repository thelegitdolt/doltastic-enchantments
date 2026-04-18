package com.dolthhaven.doltasticenchantments.core.mixin;

import com.dolthhaven.doltasticenchantments.core.utils.BookUtil;
import com.dolthhaven.doltasticenchantments.core.data.server.tags.DETags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.alfie.immersiveenchanting.item.AncientBook;
import me.alfie.immersiveenchanting.util.EnchantmentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(AncientBook.class)
public class AncientBookMixin extends Item {
    public AncientBookMixin(Properties pProperties) {
        super(pProperties);
    }

    @WrapOperation(method = "appendHoverText", slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;"),
            to = @At(value = "INVOKE", target = "Lme/alfie/immersiveenchanting/datacomponent/ReplicatedNBT;isReplicated(Lnet/minecraft/world/item/ItemStack;)Z")
    ), at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private <E> boolean DoltasticEnchantments$RenderEveryEnchantment(List<Component> instance, E e, Operation<Boolean> original, @Local(argsOnly = true) ItemStack book, @Local RegistryAccess access) {
        List<ResourceKey<Enchantment>> enchants = BookUtil.getAllStoredEnchantments(book);
        if (enchants.size() == 1) return original.call(instance, e);
        MutableComponent loreText = Component.translatable(AncientBook.TRANSLATION_KEY);
        loreText.append(":");
        instance.add(loreText);
        for (ResourceKey<Enchantment> enchantKey : enchants) {
            Holder<Enchantment> holder = EnchantmentUtil.getEnchantmentHolder(access, enchantKey).orElse(null);
            if (holder == null) continue;

            Enchantment enchantment = holder.value();
            String name = enchantment.getDescriptionId();
            instance.add(Component.literal("- ").append(Component.translatable(name)).withStyle(ChatFormatting.GOLD));
        }
        return true;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return BookUtil.getAllStoredEnchantments(stack).size() > 1  ||
                ForgeRegistries.ENCHANTMENTS.tags().getTag(DETags.Enchantments.TREASURE).contains(ForgeRegistries.ENCHANTMENTS.getValue(AncientBook.getStoredEnchantment(stack).location())) ? Rarity.RARE : Rarity.UNCOMMON;
    }
}

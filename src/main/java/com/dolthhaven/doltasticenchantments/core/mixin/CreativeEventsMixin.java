//package com.dolthhaven.doltasticenchantments.core.mixin;
//
//import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
//import com.dolthhaven.doltasticenchantments.integration.DEReliableRemoverCompat;
//import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
//import me.alfie.immersiveenchanting.creativetab.ModCreativeTab;
//import me.alfie.immersiveenchanting.util.EnchantmentUtil;
//import net.minecraft.core.Holder;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.enchantment.Enchantment;
//import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//
//@Mixin(ModCreativeTab.class)
//public class CreativeEventsMixin {
//    @WrapWithCondition(method = "buildCreativeTab", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/BuildCreativeModeTabContentsEvent;accept(Lnet/minecraft/world/item/ItemStack;)V"))
//    private static boolean DoltasticEnchantments$HideReliablyRemovedBooks(BuildCreativeModeTabContentsEvent instance, ItemStack stack) {
//        Holder<Enchantment> enchant = EnchantmentUtil.getStoredEnchantment(stack);
//        if (DoltasticEnchantments.reliableRemover() && DEReliableRemoverCompat.isEnchantmentRemoved(enchant)) return false;
//        return true;
//    }
//}

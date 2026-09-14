package com.dolthhaven.doltasticenchantments.core.data.server.tags;

import com.dolthhaven.doltasticenchantments.core.DoltasticEnchantments;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class DETags {
    public static class Enchantments {
        public static final TagKey<Enchantment> DOESNT_REQUIRE_BOOKS = TagUtil.enchantmentTag(DoltasticEnchantments.MOD_ID, "doesnt_require_books");
        public static final TagKey<Enchantment> UNIVERSAL_ENCHANTS = TagUtil.enchantmentTag(DoltasticEnchantments.MOD_ID, "universal");
        public static final TagKey<Enchantment> TREASURE = TagUtil.enchantmentTag(DoltasticEnchantments.MOD_ID, "treasure");
    }
}

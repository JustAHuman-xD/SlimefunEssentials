package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

public record TagStack(TagKey<Item> tagKey, long amount) {}

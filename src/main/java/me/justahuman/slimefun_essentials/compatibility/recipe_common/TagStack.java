package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

public record TagStack(@Getter TagKey<Item> tagKey, @Getter long amount) {}

package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.List;

public record RecipeContainer(@Getter Identifier id, @Getter String type, @Getter List<CustomMultiStack> inputs, @Getter List<CustomMultiStack> outputs, @Getter Integer ticks, @Getter Integer energy) {}

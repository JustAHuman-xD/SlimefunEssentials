package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import net.minecraft.util.Identifier;

import java.util.List;

public record RecipeContainer(Identifier id, String type, List<CustomMultiStack> inputs, List<CustomMultiStack> outputs, Float time, Integer energy, List<ConditionContainer> conditions) {}

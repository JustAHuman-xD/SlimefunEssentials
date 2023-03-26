package me.justahuman.slimefun_essentials.api;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public interface RecipeRenderer {
    int getContentsWidth();
    int getContentsHeight();

    default int getDisplayWidth() {
        return getContentsWidth() + TextureUtils.padding * 2;
    }

    default int getDisplayHeight() {
        return getContentsHeight() + TextureUtils.padding * 2;
    }

    default int calculateXOffset() {
        return (getDisplayWidth() - getContentsWidth()) / 2;
    }

    default int calculateYOffset(int height) {
        return (getDisplayHeight() - height) / 2;
    }

    default boolean hasLabels(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.labels() != null && !slimefunRecipe.labels().isEmpty();
    }

    default boolean hasEnergy(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.energy() != null && slimefunRecipe.energy() != 0;
    }

    default boolean hasInputs(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.inputs() != null && !slimefunRecipe.inputs().isEmpty();
    }

    default boolean hasTime(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.time() != null;
    }

    default boolean hasSpeed(SlimefunCategory slimefunCategory) {
        return slimefunCategory.speed() != null;
    }

    default boolean hasOutputs(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.outputs() != null && !slimefunRecipe.outputs().isEmpty();
    }
}

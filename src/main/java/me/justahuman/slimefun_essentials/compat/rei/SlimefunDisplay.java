package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlimefunDisplay implements Display {
    protected final RecipeCategory recipeCategory;
    protected final SlimefunRecipe slimefunRecipe;
    protected final List<EntryIngredient> inputs = new ArrayList<>();
    protected final List<EntryIngredient> outputs = new ArrayList<>();

    public SlimefunDisplay(RecipeCategory recipeCategory, SlimefunRecipe slimefunRecipe) {
        this.recipeCategory = recipeCategory;
        this.slimefunRecipe = slimefunRecipe;
        this.inputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getInputEntries(slimefunRecipe));
        this.outputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getOutputEntries(slimefunRecipe));
    }

    public SlimefunRecipe slimefunRecipe() {
        return this.slimefunRecipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputs;
    }

    @Override
    public @NotNull CategoryIdentifier<SlimefunDisplay> getCategoryIdentifier() {
        return CategoryIdentifier.of(SlimefunEssentials.id(this.recipeCategory.id().toLowerCase()));
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}

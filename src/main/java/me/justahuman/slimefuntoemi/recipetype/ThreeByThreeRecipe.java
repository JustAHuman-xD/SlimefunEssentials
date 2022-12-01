package me.justahuman.slimefuntoemi.recipetype;

import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class ThreeByThreeRecipe extends EmiCraftingRecipe {

    private final EmiRecipeCategory emiRecipeCategory;

    public ThreeByThreeRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs) {
        super(inputs, outputs.isEmpty() ? EmiStack.EMPTY : outputs.get(0), id);
        this.emiRecipeCategory = emiRecipeCategory;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.emiRecipeCategory;
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

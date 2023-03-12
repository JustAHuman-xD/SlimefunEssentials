package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.List;

public class ReactorRecipe extends ProcessRecipe {
    public ReactorRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer energy, Integer time) {
        super(category, inputs, outputs, null, energy, time, null);
    }
    
    @Override
    public int getWidgetsWidth() {
        return 0;
    }
    
    @Override
    public int getWidgetsHeight() {
        return 0;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
    
    }
}

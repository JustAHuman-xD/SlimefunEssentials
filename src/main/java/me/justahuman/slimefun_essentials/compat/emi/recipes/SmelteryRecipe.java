package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.utils.Utils;

import java.util.List;

public class SmelteryRecipe extends ProcessRecipe {
    public SmelteryRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer energy, Integer time, Integer speed) {
        super(category, inputs, outputs, null, energy, time, speed);
        Utils.fillInputs(inputs, 6);
        Utils.fillOutputs(outputs, 1);
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

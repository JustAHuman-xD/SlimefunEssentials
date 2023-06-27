package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public class SmelteryRecipe extends ProcessRecipe {
    public SmelteryRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        super(Type.SMELTERY, slimefunCategory, slimefunRecipe, emiRecipeCategory);

        EmiUtils.fillInputs(this.inputs, 6);
        EmiUtils.fillOutputs(this.outputs, 1);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final OffsetBuilder offsets = new OffsetBuilder(this, this.slimefunRecipe, TextureUtils.PADDING, TextureUtils.PADDING);
        
        // Display Energy
        addEnergyWithCheck(widgets, offsets);
    
    
        int i = 0;
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 2; x++) {
                widgets.addSlot(this.inputs.get(i), offsets.getX(), offsets.getY());
                offsets.x().addSlot(false);
                i++;
            }
            offsets.x().subtract(TextureUtils.SLOT_SIZE * 2);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.SLOT_SIZE * 2).addPadding();
    
        // Display Arrow
        addArrowWithCheck(widgets, offsets);
    
        // Display Outputs
        addOutputs(widgets, offsets);
    }
}

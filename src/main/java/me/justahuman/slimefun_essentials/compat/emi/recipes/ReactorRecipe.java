package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public class ReactorRecipe extends ProcessRecipe {
    public ReactorRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        super(Type.REACTOR, slimefunCategory, slimefunRecipe, emiRecipeCategory);

        EmiUtils.fillInputs(this.inputs, 4);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final OffsetBuilder offsets = new OffsetBuilder(this, this.slimefunRecipe, TextureUtils.padding, TextureUtils.padding);

        widgets.addSlot(this.inputs.get(0), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(offsets.getX(), offsets.getY());
        offsets.x().addSlot();

        addArrowWithCheck(widgets, offsets.getX(), offsets.getY(), false);
        offsets.x().addArrow();
        
        if (this.slimefunRecipe.hasOutputs()) {
            widgets.addSlot(this.outputs.get(0), offsets.getX(), offsets.getY()).output(true);
        }
        
        if (this.slimefunRecipe.hasEnergy()) {
            addEnergy(widgets, offsets.getX() + (this.slimefunRecipe.hasOutputs() ? (TextureUtils.outputSize - TextureUtils.energyWidth) / 2 : 0), offsets.getY() + (this.slimefunRecipe.hasOutputs() ? - TextureUtils.energyHeight - TextureUtils.padding : TextureUtils.padding));
            offsets.x().add(this.slimefunRecipe.hasOutputs() ? TextureUtils.outputSize : TextureUtils.energyWidth).addPadding();
        }
        
        addArrowWithCheck(widgets, offsets.getX(), offsets.getY(), true);
        offsets.x().addArrow();
        offsets.y().subtract(TextureUtils.slotSize * 2);
        widgets.addSlot(inputs.get(1), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(inputs.get(2), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(inputs.get(3), offsets.getX(), offsets.getY());
    }
}

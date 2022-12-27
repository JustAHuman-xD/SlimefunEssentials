package me.justahuman.slimefun_essentials.compatibility.emi.recipetype;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.compatibility.emi.EmiUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public class ThreeByThreeRecipe implements EmiRecipe {
    
    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final int ticks;
    private final int energy;
    
    public ThreeByThreeRecipe(EmiRecipeCategory emirecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs, int ticks, int energy) {
        this.emiRecipeCategory = emirecipeCategory;
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.ticks = ticks;
        this.energy = energy;
    }
    
    @Override
    public EmiRecipeCategory getCategory() {
        return this.emiRecipeCategory;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }
    
    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }
    
    @Override
    public int getDisplayWidth() {
        return 118;
    }
    
    @Override
    public int getDisplayHeight() {
        return 54;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offsetY = (getDisplayHeight() - (hasEnergy() ? EmiUtils.chargeHeight + 4 + EmiUtils.arrowHeight : EmiUtils.arrowHeight)) / 2;
        
        //Display Energy
        if (hasEnergy()) {
            widgets.addTexture(EmiUtils.EMPTY_CHARGE, 60 + (EmiUtils.arrowWidth - EmiUtils.chargeWidth) / 2, offsetY);
            widgets.addAnimatedTexture(this.energy > 0 ? EmiUtils.GAIN_CHARGE : EmiUtils.LOOSE_CHARGE, 60 + (EmiUtils.arrowWidth - EmiUtils.chargeWidth) / 2, offsetY, 1000, false, this.energy <= 0, this.energy <= 0).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.energy." + (this.energy > 0 ? "generate" : "use"), EmiUtils.numberFormat.format(Math.abs(this.energy)))))));
        }
        
        //Display Arrow
        if (hasTime()) {
            widgets.addFillingArrow(60, (hasEnergy() ? (offsetY + EmiUtils.chargeHeight + EmiUtils.padding) : offsetY), this.ticks).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(ticks / 20f), EmiUtils.numberFormat.format(ticks))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
        }
        
        //Display Inputs
        for (int i = 0; i < 9; i++) {
            if (i < this.inputs.size()) {
                widgets.addSlot(this.inputs.get(i), i % 3 * 18, i / 3 * 18);
            } else {
                widgets.addSlot(i % 3 * 18, i / 3 * 18);
            }
        }
        
        //Display Outputs
        widgets.addSlot(this.outputs.isEmpty() ? EmiStack.EMPTY : this.outputs.get(0), 92, 14).output(true).recipeContext(this);
    }
    
    private boolean hasTime() {
        return this.ticks > 0;
    }
    
    private boolean hasEnergy() {
        return this.energy != 0;
    }
}

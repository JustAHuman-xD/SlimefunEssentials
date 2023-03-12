package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class GridRecipe extends ProcessRecipe {
    protected final int side;
    public GridRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer energy, Integer time, Integer speed, String type) {
        super(category, inputs, outputs, null, energy, time, speed);
        
        int length;
        try {
            length = Integer.parseInt(type.substring(type.length() - 1));
        } catch (NumberFormatException ignored) {
            length = 3;
        }
        side = length;
        Utils.fillInputs(inputs, side * side);
    }
    
    @Override
    public int getWidgetsWidth() {
        return (side * EmiUtils.slot + EmiUtils.padding) + (hasEnergy() ? EmiUtils.chargeWidth + EmiUtils.padding : 0) + (EmiUtils.arrowWidth + EmiUtils.padding) + (hasOutputs() ? EmiUtils.bigSlot * this.outputs.size() : 0);
    }
    
    @Override
    public int getWidgetsHeight() {
        return (side * EmiUtils.slot);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        int offsetX = EmiUtils.padding;
        int offsetY = EmiUtils.padding;
        final int offsetYCharge = (getDisplayHeight() - EmiUtils.chargeHeight) / 2;
        final int offsetYArrow = (getDisplayHeight() - EmiUtils.arrowHeight) / 2;
        final int offsetYBig = (getDisplayHeight() - EmiUtils.bigSlot) / 2;
    
        // Display Energy
        if (hasEnergy() && hasOutputs()) {
            addEnergyDisplay(widgets, offsetX, offsetYCharge);
            offsetX += EmiUtils.chargeWidth + EmiUtils.padding;
        }
        
        int i = 0;
        for (int y = 1; y <= side; y++) {
            for (int x = 1; x <= side; x++) {
                widgets.addSlot(inputs.get(i), offsetX, offsetY);
                offsetX += EmiUtils.slot;
                i++;
            }
            offsetX = EmiUtils.padding;
            offsetY += EmiUtils.slot;
        }
        offsetX += EmiUtils.slot * side + EmiUtils.padding;
    
        // Display Time
        if (hasTime()) {
            final int sfTicks = this.time / 10 / (hasSpeed() ? this.speed : 1);
            final int millis =  sfTicks * 2000;
            widgets.addFillingArrow(offsetX, offsetYArrow, millis).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(sfTicks / 2f), EmiUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetYArrow);
        }
        offsetX += EmiUtils.arrowWidth + EmiUtils.padding;
    
        // Display Outputs
        if (hasOutputs()) {
            for (EmiStack output : this.outputs) {
                widgets.addSlot(output, offsetX, offsetYBig).output(true);
                offsetX += EmiUtils.bigSlot + EmiUtils.padding;
            }
        } else if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX, offsetYCharge);
        }
    }
}

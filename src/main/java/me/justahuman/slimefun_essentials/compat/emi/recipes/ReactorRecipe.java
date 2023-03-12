package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.compat.emi.ReverseFillingArrowWidget;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class ReactorRecipe extends ProcessRecipe {
    public ReactorRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer energy, Integer time) {
        super(category, inputs, outputs, null, energy, time, null);
        Utils.fillInputs(inputs, 4);
        Utils.fillOutputs(outputs, 1);
    }
    
    @Override
    public int getWidgetsWidth() {
        return (EmiUtils.slot + EmiUtils.arrowWidth) * 2 + EmiUtils.padding * 4 + EmiUtils.bigSlot;
    }
    
    @Override
    public int getWidgetsHeight() {
        return EmiUtils.slot * 2 + EmiUtils.bigSlot;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        int offsetX = EmiUtils.padding;
        int offsetY = EmiUtils.padding;
        
        widgets.addSlot(inputs.get(0), offsetX, offsetY);
        offsetY += EmiUtils.slot;
        widgets.addSlot(offsetX, offsetY);
        offsetY += EmiUtils.slot;
        widgets.addSlot(offsetX, offsetY);
        offsetX += EmiUtils.slot + EmiUtils.padding;
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.time / 10 / (hasSpeed() ? this.speed : 1));
            final int millis =  sfTicks * 500;
            widgets.add(new FillingArrowWidget(offsetX, offsetY, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(sfTicks / 2f), EmiUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetY);
        }
        offsetX += EmiUtils.arrowWidth + EmiUtils.padding;
        widgets.addSlot(outputs.get(0), offsetX, offsetY).output(true);
        if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX + (EmiUtils.bigSlot - EmiUtils.chargeWidth) / 2, offsetY - EmiUtils.chargeHeight - EmiUtils.padding);
        }
        offsetX += EmiUtils.bigSlot + EmiUtils.padding;
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.time / 10 / (hasSpeed() ? this.speed : 1));
            final int millis =  sfTicks * 500;
            widgets.add(new ReverseFillingArrowWidget(offsetX, offsetY, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(sfTicks / 2f), EmiUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiUtils.BACKWARDS_EMPTY_ARROW, offsetX, offsetY);
        }
        offsetX += EmiUtils.arrowWidth + EmiUtils.padding;
        offsetY = EmiUtils.padding;
        widgets.addSlot(inputs.get(1), offsetX, offsetY);
        offsetY += EmiUtils.slot;
        widgets.addSlot(inputs.get(2), offsetX, offsetY);
        offsetY += EmiUtils.slot;
        widgets.addSlot(inputs.get(3), offsetX, offsetY);
    }
}

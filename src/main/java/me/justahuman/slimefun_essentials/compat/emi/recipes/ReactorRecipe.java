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
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class ReactorRecipe extends ProcessRecipe {
    public ReactorRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer energy, Integer time) {
        super(category, inputs, outputs, null, energy, time, null);
        Utils.fillInputs(inputs, 4);
    }
    
    @Override
    public int getWidgetsWidth() {
        return (TextureUtils.slot + TextureUtils.arrowWidth) * 2 + TextureUtils.padding * 4 + (hasOutputs() ? TextureUtils.bigSlot : TextureUtils.chargeWidth);
    }
    
    @Override
    public int getWidgetsHeight() {
        return TextureUtils.slot * 2 + (hasOutputs() ? TextureUtils.bigSlot : TextureUtils.slot);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        int offsetX = TextureUtils.padding;
        int offsetY = TextureUtils.padding;
        
        widgets.addSlot(inputs.get(0), offsetX, offsetY);
        offsetY += TextureUtils.slot;
        widgets.addSlot(offsetX, offsetY);
        offsetY += TextureUtils.slot;
        widgets.addSlot(offsetX, offsetY);
        offsetX += TextureUtils.slot + TextureUtils.padding;
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.time / 10 / (hasSpeed() ? this.speed : 1));
            final int millis =  sfTicks * 500;
            widgets.add(new FillingArrowWidget(offsetX, offsetY, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(sfTicks / 2f), EmiUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetY);
        }
        offsetX += TextureUtils.arrowWidth + TextureUtils.padding;
        
        if (hasOutputs()) {
            widgets.addSlot(outputs.get(0), offsetX, offsetY).output(true);
        }
        
        if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX + (hasOutputs() ? (TextureUtils.bigSlot - TextureUtils.chargeWidth) / 2 : 0), offsetY + (hasOutputs() ? - TextureUtils.chargeHeight - TextureUtils.padding : TextureUtils.padding));
            offsetX += (hasOutputs() ? TextureUtils.bigSlot  : TextureUtils.chargeWidth) + + TextureUtils.padding;
        }
        
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.time / 10 / (hasSpeed() ? this.speed : 1));
            final int millis =  sfTicks * 500;
            widgets.add(new ReverseFillingArrowWidget(offsetX, offsetY, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(sfTicks / 2f), EmiUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiUtils.BACKWARDS_EMPTY_ARROW, offsetX, offsetY);
        }
        offsetX += TextureUtils.arrowWidth + TextureUtils.padding;
        offsetY = TextureUtils.padding;
        widgets.addSlot(inputs.get(1), offsetX, offsetY);
        offsetY += TextureUtils.slot;
        widgets.addSlot(inputs.get(2), offsetX, offsetY);
        offsetY += TextureUtils.slot;
        widgets.addSlot(inputs.get(3), offsetX, offsetY);
    }
}

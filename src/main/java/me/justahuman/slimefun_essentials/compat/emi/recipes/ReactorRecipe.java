package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.compat.emi.ReverseFillingArrowWidget;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class ReactorRecipe extends ProcessRecipe {
    public ReactorRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        super(slimefunCategory, slimefunRecipe, emiRecipeCategory);
        Utils.fillInputs(this.inputs, 4);
    }
    
    @Override
    public int getContentsWidth() {
        return (TextureUtils.slotSize + TextureUtils.arrowWidth) * 2 + TextureUtils.padding * 4 + (hasOutputs(this.slimefunRecipe) ? TextureUtils.outputSize : TextureUtils.energyWidth);
    }
    
    @Override
    public int getContentsHeight() {
        return TextureUtils.slotSize * 2 + (hasOutputs(this.slimefunRecipe) ? TextureUtils.outputSize : TextureUtils.slotSize);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        int offsetX = TextureUtils.padding;
        int offsetY = TextureUtils.padding;
        
        widgets.addSlot(this.inputs.get(0), offsetX, offsetY);
        offsetY += TextureUtils.slotSize;
        widgets.addSlot(offsetX, offsetY);
        offsetY += TextureUtils.slotSize;
        widgets.addSlot(offsetX, offsetY);
        offsetX += TextureUtils.slotSize + TextureUtils.padding;
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.slimefunRecipe.time() / 10 / (hasSpeed() ? this.slimefunCategory.speed() : 1));
            final int millis =  sfTicks * 500;
            widgets.add(new FillingArrowWidget(offsetX, offsetY, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", TextureUtils.numberFormat.format(sfTicks / 2f), TextureUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetY);
        }
        offsetX += TextureUtils.arrowWidth + TextureUtils.padding;
        
        if (hasOutputs()) {
            widgets.addSlot(this.outputs.get(0), offsetX, offsetY).output(true);
        }
        
        if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX + (hasOutputs() ? (TextureUtils.outputSize - TextureUtils.energyWidth) / 2 : 0), offsetY + (hasOutputs() ? - TextureUtils.energyHeight - TextureUtils.padding : TextureUtils.padding));
            offsetX += (hasOutputs() ? TextureUtils.outputSize : TextureUtils.energyWidth) + + TextureUtils.padding;
        }
        
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.slimefunRecipe.time() / 10 / (hasSpeed() ? this.slimefunCategory.speed() : 1));
            final int millis =  sfTicks * 500;
            widgets.add(new ReverseFillingArrowWidget(offsetX, offsetY, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", TextureUtils.numberFormat.format(sfTicks / 2f), TextureUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiUtils.BACKWARDS_EMPTY_ARROW, offsetX, offsetY);
        }
        offsetX += TextureUtils.arrowWidth + TextureUtils.padding;
        offsetY = TextureUtils.padding;
        widgets.addSlot(inputs.get(1), offsetX, offsetY);
        offsetY += TextureUtils.slotSize;
        widgets.addSlot(inputs.get(2), offsetX, offsetY);
        offsetY += TextureUtils.slotSize;
        widgets.addSlot(inputs.get(3), offsetX, offsetY);
    }
}

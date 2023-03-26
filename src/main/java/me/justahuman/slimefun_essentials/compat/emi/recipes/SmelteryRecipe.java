package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class SmelteryRecipe extends ProcessRecipe {
    public SmelteryRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        super(slimefunCategory, slimefunRecipe, emiRecipeCategory);
        Utils.fillInputs(this.inputs, 6);
        Utils.fillOutputs(this.outputs, 1);
    }
    
    @Override
    public int getContentsWidth() {
        return (hasEnergy() ? TextureUtils.energyWidth + TextureUtils.padding : 0) + (hasInputs() ? TextureUtils.slotSize * 2 + TextureUtils.padding : TextureUtils.slotSize + TextureUtils.padding) + (TextureUtils.arrowWidth + TextureUtils.padding) + (hasOutputs() ? TextureUtils.outputSize * this.outputs.size() + TextureUtils.padding * (this.outputs.size() - 1): 0);
    }
    
    @Override
    public int getContentsHeight() {
        return TextureUtils.slotSize * 3;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offsetYCharge = (getDisplayHeight() - TextureUtils.energyHeight) / 2;
        final int offsetYArrow = (getDisplayHeight() - TextureUtils.arrowHeight) / 2;
        final int offsetYBig = (getDisplayHeight() - TextureUtils.outputSize) / 2;
        int offsetX = TextureUtils.padding;
        int offsetY = TextureUtils.padding;
        
        // Display Energy
        if (hasEnergy() && hasOutputs()) {
            addEnergyDisplay(widgets, offsetX, offsetYCharge);
            offsetX += TextureUtils.energyWidth + TextureUtils.padding;
        }
    
    
        int i = 0;
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 2; x++) {
                widgets.addSlot(this.inputs.get(i), offsetX, offsetY);
                offsetX += TextureUtils.slotSize;
                i++;
            }
            offsetX -= TextureUtils.slotSize * 2;
            offsetY += TextureUtils.slotSize;
        }
        offsetX += TextureUtils.slotSize * 2 + TextureUtils.padding;
    
        // Display Time
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.slimefunRecipe.time() / 10 / (hasSpeed() ? this.slimefunCategory.speed() : 1));
            final int millis =  sfTicks * 500;
            widgets.addFillingArrow(offsetX, offsetYArrow, millis).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", TextureUtils.numberFormat.format(sfTicks / 2f), TextureUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetYArrow);
        }
        offsetX += TextureUtils.arrowWidth + TextureUtils.padding;
    
        // Display Outputs
        if (hasOutputs()) {
            for (EmiStack output : this.outputs) {
                widgets.addSlot(output, offsetX, offsetYBig).output(true);
                offsetX += TextureUtils.outputSize + TextureUtils.padding;
            }
        } else if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX, offsetYCharge);
        }
    }
}

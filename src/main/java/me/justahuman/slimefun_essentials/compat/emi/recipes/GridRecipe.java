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

public class GridRecipe extends ProcessRecipe {
    protected final int side;
    public GridRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory, String type) {
        super(slimefunCategory, slimefunRecipe, emiRecipeCategory);
        
        int length;
        try {
            length = Integer.parseInt(type.substring(type.length() - 1));
        } catch (NumberFormatException ignored) {
            length = 3;
        }
        this.side = length;
        Utils.fillInputs(this.inputs, this.side * this.side);
    }
    
    @Override
    public int getContentsWidth() {
        return (this.side * TextureUtils.slotSize + TextureUtils.padding) + (hasEnergy(this.slimefunRecipe) ? TextureUtils.energyWidth + TextureUtils.padding : 0) + (TextureUtils.arrowWidth + TextureUtils.padding) + (hasOutputs(this.slimefunRecipe) ? TextureUtils.outputSize * this.outputs.size() : 0);
    }
    
    @Override
    public int getContentsHeight() {
        return (this.side * TextureUtils.slotSize);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int energyOffset = calculateYOffset(TextureUtils.energyHeight);
        final int arrowOffset = calculateYOffset(TextureUtils.arrowHeight);
        final int outputOffset = calculateYOffset(TextureUtils.outputSize);
        int offsetX = calculateXOffset();
        int offsetY = TextureUtils.padding;
    
        // Display Energy
        if (hasEnergy() && hasOutputs()) {
            addEnergyDisplay(widgets, offsetX, energyOffset);
            offsetX += TextureUtils.energyWidth + TextureUtils.padding;
        }
        
        int i = 0;
        for (int y = 1; y <= this.side; y++) {
            for (int x = 1; x <= this.side; x++) {
                widgets.addSlot(this.inputs.get(i), offsetX, offsetY);
                offsetX += TextureUtils.slotSize;
                i++;
            }
            offsetX = TextureUtils.padding;
            offsetY += TextureUtils.slotSize;
        }
        offsetX += TextureUtils.slotSize * side + TextureUtils.padding;
    
        // Display Time
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.slimefunRecipe.time() / 10 / (hasSpeed() ? this.slimefunCategory.speed() : 1));
            final int millis =  sfTicks * 500;
            widgets.addFillingArrow(offsetX, arrowOffset, millis).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", TextureUtils.numberFormat.format(sfTicks / 2f), TextureUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, arrowOffset);
        }
        offsetX += TextureUtils.arrowWidth + TextureUtils.padding;
    
        // Display Outputs
        if (hasOutputs()) {
            for (EmiStack output : this.outputs) {
                widgets.addSlot(output, offsetX, outputOffset).output(true);
                offsetX += TextureUtils.outputSize + TextureUtils.padding;
            }
        } else if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX, energyOffset);
        }
    }
}

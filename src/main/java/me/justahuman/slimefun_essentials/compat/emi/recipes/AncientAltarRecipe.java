package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;

public class AncientAltarRecipe extends ProcessRecipe {
    public AncientAltarRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        super(slimefunCategory, slimefunRecipe, emiRecipeCategory);
        Utils.fillInputs(this.inputs, 9);
        Utils.fillOutputs(this.outputs, 1);
    }
    
    @Override
    public int getDisplayWidth() {
        return 146;
    }
    
    @Override
    public int getDisplayHeight() {
        return 88;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offsetYS = (getDisplayHeight() - TextureUtils.slotSize) / 2;
        final int offsetYA = (getDisplayHeight() - TextureUtils.arrowHeight) /2;
        int offsetX = getDisplayWidth() / 2 - (TextureUtils.slotSize * 5 + TextureUtils.arrowWidth + TextureUtils.outputSize + 8) / 2;
        
        widgets.addSlot(this.inputs.get(3), offsetX, offsetYS).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slotSize;
        widgets.addSlot(this.inputs.get(0), offsetX, offsetYS + TextureUtils.slotSize).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        widgets.addSlot(this.inputs.get(6), offsetX, offsetYS - TextureUtils.slotSize).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slotSize;
        widgets.addSlot(this.inputs.get(1), offsetX, offsetYS + TextureUtils.slotSize * 2).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        widgets.addSlot(this.inputs.get(4), offsetX, offsetYS).backgroundTexture(TextureUtils.WIDGETS, 18, 0);
        widgets.addSlot(this.inputs.get(7), offsetX, offsetYS - TextureUtils.slotSize * 2).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slotSize;
        widgets.addSlot(this.inputs.get(2), offsetX, offsetYS + TextureUtils.slotSize).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        widgets.addSlot(this.inputs.get(8), offsetX, offsetYS - TextureUtils.slotSize).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slotSize;
        widgets.addSlot(this.inputs.get(5), offsetX, offsetYS).backgroundTexture(TextureUtils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slotSize + 4;
        widgets.addFillingArrow(offsetX, offsetYA, this.slimefunRecipe.time() * 1000);
        offsetX = offsetX + TextureUtils.arrowWidth + 4;
        widgets.addSlot(this.outputs.get(0), offsetX, offsetYS);
    }
    
    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

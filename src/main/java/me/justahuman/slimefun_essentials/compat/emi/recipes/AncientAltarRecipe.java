package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;

import java.util.List;

public class AncientAltarRecipe extends ProcessRecipe {
    public AncientAltarRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer time) {
        super(category, inputs, outputs, null, null, time, null);
        Utils.fillInputs(inputs, 9);
        Utils.fillOutputs(outputs, 1);
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
        final int offsetYS = (getDisplayHeight() - TextureUtils.slot) / 2;
        final int offsetYA = (getDisplayHeight() - TextureUtils.arrowHeight) /2;
        int offsetX = getDisplayWidth() / 2 - (TextureUtils.slot * 5 + TextureUtils.arrowWidth + TextureUtils.bigSlot + 8) / 2;
        
        widgets.addSlot(inputs.get(3), offsetX, offsetYS).backgroundTexture(Utils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slot;
        widgets.addSlot(inputs.get(0), offsetX, offsetYS + TextureUtils.slot).backgroundTexture(Utils.WIDGETS, 0, 0);
        widgets.addSlot(inputs.get(6), offsetX, offsetYS - TextureUtils.slot).backgroundTexture(Utils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slot;
        widgets.addSlot(inputs.get(1), offsetX, offsetYS + TextureUtils.slot * 2).backgroundTexture(Utils.WIDGETS, 0, 0);
        widgets.addSlot(inputs.get(4), offsetX, offsetYS).backgroundTexture(Utils.WIDGETS, 18, 0);
        widgets.addSlot(inputs.get(7), offsetX, offsetYS - TextureUtils.slot * 2).backgroundTexture(Utils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slot;
        widgets.addSlot(inputs.get(2), offsetX, offsetYS + TextureUtils.slot).backgroundTexture(Utils.WIDGETS, 0, 0);
        widgets.addSlot(inputs.get(8), offsetX, offsetYS - TextureUtils.slot).backgroundTexture(Utils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slot;
        widgets.addSlot(inputs.get(5), offsetX, offsetYS).backgroundTexture(Utils.WIDGETS, 0, 0);
        offsetX = offsetX + TextureUtils.slot + 4;
        widgets.addFillingArrow(offsetX, offsetYA, time * 1000);
        offsetX = offsetX + TextureUtils.arrowWidth + 4;
        widgets.addSlot(outputs.get(0), offsetX, offsetYS);
    }
    
    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

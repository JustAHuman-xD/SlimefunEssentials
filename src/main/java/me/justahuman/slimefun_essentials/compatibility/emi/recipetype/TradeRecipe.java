package me.justahuman.slimefun_essentials.compatibility.emi.recipetype;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.compatibility.emi.EmiUtils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradeRecipe implements EmiRecipe {

    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    
    public TradeRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs) {
        this.emiRecipeCategory = emiRecipeCategory;
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.emiRecipeCategory;
    }

    @Override
    public @Nullable Identifier getId() {

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

        return 128;
    }

    @Override
    public int getDisplayHeight() {

        return 35;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offsetX = getDisplayWidth() / 2 - (EmiUtils.slotWidth + EmiUtils.bigSlotWidth + EmiUtils.arrowWidth + EmiUtils.slotWidth + 12) /2;
        final int offsetYS = (getDisplayHeight() - EmiUtils.slotHeight) / 2;
        final int offsetYO = (getDisplayHeight() - EmiUtils.bigSlotHeight) / 2;
        final int offsetYA = (getDisplayHeight() - EmiUtils.arrowHeight) / 2;

        widgets.addSlot(inputs.get(0), offsetX, offsetYS);
        widgets.addSlot(inputs.get(1), offsetX + EmiUtils.slotWidth + 4, offsetYO).output(true);
        widgets.addFillingArrow(offsetX + EmiUtils.slotWidth + EmiUtils.bigSlotWidth + 8, offsetYA, 2000);
        widgets.addSlot(outputs.get(0), offsetX + EmiUtils.slotWidth + EmiUtils.bigSlotWidth + EmiUtils.arrowWidth + 12, offsetYS);
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

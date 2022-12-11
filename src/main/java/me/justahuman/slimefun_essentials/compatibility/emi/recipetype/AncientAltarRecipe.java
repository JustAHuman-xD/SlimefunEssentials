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

public class AncientAltarRecipe implements EmiRecipe {

    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public AncientAltarRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs) {
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

        return 146;
    }

    @Override
    public int getDisplayHeight() {

        return 88;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int offsetX = getDisplayWidth() / 2 - (EmiUtils.slotWidth * 5 + EmiUtils.arrowWidth + EmiUtils.bigSlotWidth + 8) / 2;
        final int offsetYS = (getDisplayHeight() - EmiUtils.slotHeight) / 2;
        final int offsetYA = (getDisplayHeight() - EmiUtils.arrowHeight) /2;
        final int distance = EmiUtils.slotHeight;

        widgets.addSlot(inputs.size() >= 4 ? inputs.get(3) : EmiStack.EMPTY, offsetX, offsetYS).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        offsetX = offsetX + EmiUtils.slotWidth;
        widgets.addSlot(! inputs.isEmpty() ? inputs.get(0) : EmiStack.EMPTY, offsetX, offsetYS + distance).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        widgets.addSlot(inputs.size() >= 7 ? inputs.get(6) : EmiStack.EMPTY, offsetX, offsetYS - distance).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        offsetX = offsetX + EmiUtils.slotWidth;
        widgets.addSlot(inputs.size() >= 2 ? inputs.get(1) : EmiStack.EMPTY, offsetX, offsetYS + distance * 2).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        widgets.addSlot(inputs.size() >= 5 ? inputs.get(4) : EmiStack.EMPTY, offsetX, offsetYS).backgroundTexture(EmiUtils.WIDGETS, 18, 0);
        widgets.addSlot(inputs.size() >= 8 ? inputs.get(7) : EmiStack.EMPTY, offsetX, offsetYS - distance * 2).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        offsetX = offsetX + EmiUtils.slotWidth;
        widgets.addSlot(inputs.size() >= 3 ? inputs.get(2) : EmiStack.EMPTY, offsetX, offsetYS + distance).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        widgets.addSlot(inputs.size() >= 9 ? inputs.get(8) : EmiStack.EMPTY, offsetX, offsetYS - distance).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        offsetX = offsetX + EmiUtils.slotWidth;
        widgets.addSlot(inputs.size() >= 6 ? inputs.get(5) : EmiStack.EMPTY, offsetX, offsetYS).backgroundTexture(EmiUtils.WIDGETS, 0, 0);
        offsetX = offsetX + EmiUtils.slotWidth + 4;
        widgets.addFillingArrow(offsetX, offsetYA, (int) (80 * 8 / 20f * 1000));
        offsetX = offsetX + EmiUtils.arrowWidth + 4;
        widgets.addSlot(! outputs.isEmpty() ? outputs.get(0) : EmiStack.EMPTY, offsetX, offsetYS);
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

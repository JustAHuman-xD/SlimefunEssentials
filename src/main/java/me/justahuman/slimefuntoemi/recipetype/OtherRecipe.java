package me.justahuman.slimefuntoemi.recipetype;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefuntoemi.Utils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OtherRecipe implements EmiRecipe {

    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public OtherRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs) {
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

        return 118;
    }

    @Override
    public int getDisplayHeight() {

        return 54;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int offsetX = getDisplayWidth() / 2 - (Utils.slotWidth * 2 + Utils.arrowWidth + Utils.bigSlotWidth + 12) /2;
        final int offsetYS = (getDisplayHeight() - Utils.slotHeight) / 2;
        final int offsetYO = (getDisplayHeight() - Utils.bigSlotHeight) / 2;
        final int offsetYA = (getDisplayHeight() - Utils.arrowHeight) / 2;

        widgets.addSlot(! inputs.isEmpty() ? inputs.get(0) : EmiStack.EMPTY, offsetX, offsetYS);
        offsetX = offsetX + Utils.slotWidth + 4;
        widgets.addSlot(inputs.size() >= 2 ? inputs.get(1) : EmiStack.EMPTY, offsetX, offsetYS);
        offsetX = offsetX + Utils.slotWidth + 4;
        widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetYA);
        offsetX = offsetX + Utils.arrowWidth + 4;
        widgets.addSlot(! outputs.isEmpty() ? outputs.get(0) : EmiStack.EMPTY, offsetX, offsetYO).output(true);
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

package me.justahuman.slimefuntoemi.recipetype;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefuntoemi.Utils;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KillRecipe implements EmiRecipe {

    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public KillRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs) {
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
        final int offsetX = getDisplayWidth() / 2 - (Utils.slotWidth + Utils.bigSlotWidth + Utils.arrowWidth + Utils.slotWidth + 12) /2;
        final int offsetYS = (getDisplayHeight() - Utils.slotHeight) / 2;
        final int offsetYO = (getDisplayHeight() - Utils.bigSlotHeight) / 2;
        final int offsetYA = (getDisplayHeight() - Utils.arrowHeight) / 2;

        widgets.addSlot(EmiStack.of(Items.DIAMOND_SWORD), offsetX, offsetYS);
        widgets.addSlot(inputs.get(0), offsetX + Utils.slotWidth + 4, offsetYO).output(true);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX + Utils.slotWidth + Utils.bigSlotWidth + 8, offsetYA);
        widgets.addSlot(outputs.get(0), offsetX + Utils.slotWidth + Utils.bigSlotWidth + Utils.arrowWidth + 12, offsetYS);
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
}

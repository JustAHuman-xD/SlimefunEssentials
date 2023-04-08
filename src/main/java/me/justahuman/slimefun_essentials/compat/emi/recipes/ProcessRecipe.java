package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiIntegration;
import me.justahuman.slimefun_essentials.compat.emi.EmiLabel;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProcessRecipe implements EmiRecipe, RecipeRenderer {
    protected final SlimefunCategory slimefunCategory;
    protected final SlimefunRecipe slimefunRecipe;
    protected final EmiRecipeCategory emiRecipeCategory;
    protected final List<EmiIngredient> inputs = new ArrayList<>();
    protected final List<EmiStack> outputs = new ArrayList<>();

    public ProcessRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        this.slimefunCategory = slimefunCategory;
        this.slimefunRecipe = slimefunRecipe;
        this.emiRecipeCategory = emiRecipeCategory;
        this.inputs.addAll(EmiIntegration.RECIPE_INTERPRETER.getInputIngredients(this.slimefunRecipe));
        this.outputs.addAll(EmiIntegration.RECIPE_INTERPRETER.getOutputStacks(this.slimefunRecipe));
    }
    
    @Override
    public EmiRecipeCategory getCategory() {
        return this.emiRecipeCategory;
    }
    
    @Override
    @Nullable
    public Identifier getId() {
        return null;
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
    public int getContentsWidth() {
        return TextureUtils.getContentsWidth(this.slimefunRecipe);
    }

    @Override
    public int getContentsHeight() {
        return TextureUtils.getContentsHeight(this.slimefunRecipe);
    }

    @Override
    public int getDisplayWidth() {
        return getContentsWidth() + TextureUtils.padding * 2;
    }
    
    @Override
    public int getDisplayHeight() {
        return getContentsHeight() + TextureUtils.padding * 2;
    }
    
    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int labelOffset = calculateYOffset(TextureUtils.labelSize);
        final int energyOffset =calculateYOffset(TextureUtils.energyHeight);
        final int slotOffset = calculateYOffset(TextureUtils.slotSize);
        final int arrowOffset = calculateYOffset(TextureUtils.arrowHeight);
        final int outputSlot = calculateYOffset(TextureUtils.outputSize);
        int offsetX = calculateXOffset();

        // Display Labels
        if (hasLabels()) {
            for (SlimefunLabel slimefunLabel : this.slimefunRecipe.labels()) {
                widgets.add(new EmiLabel(slimefunLabel, offsetX, labelOffset));
                offsetX += TextureUtils.labelSize + TextureUtils.padding;
            }
        }
    
        // Display Energy
        if (hasEnergy() && hasOutputs()) {
            addEnergyDisplay(widgets, offsetX, energyOffset);
            offsetX += TextureUtils.energyWidth + TextureUtils.padding;
        }
    
        //Display Inputs
        if (hasInputs()) {
            for (EmiIngredient input : this.inputs) {
                widgets.addSlot(input, offsetX, slotOffset);
                offsetX += TextureUtils.slotSize + TextureUtils.padding;
            }
        } else {
            widgets.addSlot((EmiIngredient) this.emiRecipeCategory.icon, offsetX, slotOffset);
            offsetX += TextureUtils.slotSize + TextureUtils.padding;
        }
    
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
                widgets.addSlot(output, offsetX, outputSlot).output(true);
                offsetX += TextureUtils.outputSize + TextureUtils.padding;
            }
        } else if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX, energyOffset);
        }
    }
    
    protected void addEnergyDisplay(WidgetHolder widgets, int offsetX, int offsetYCharge) {
        final int totalEnergy = this.slimefunRecipe.energy() * Math.max(1, this.slimefunRecipe.time() / 10 / (hasSpeed(this.slimefunCategory) ? this.slimefunCategory.speed() : 1));
        widgets.addTexture(EmiUtils.EMPTY_CHARGE, offsetX, offsetYCharge);
        widgets.addAnimatedTexture(totalEnergy >= 0 ? EmiUtils.GAIN_CHARGE : EmiUtils.LOOSE_CHARGE, offsetX, offsetYCharge, 1000, false, totalEnergy < 0, totalEnergy < 0).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.energy." + (totalEnergy >= 0 ? "generate" : "use"), TextureUtils.numberFormat.format(Math.abs(totalEnergy)))))));
    }

    protected boolean hasLabels() {
        return hasLabels(this.slimefunRecipe);
    }

    protected boolean hasEnergy() {
        return hasEnergy(this.slimefunRecipe);
    }

    protected boolean hasInputs() {
        return hasInputs(this.slimefunRecipe);
    }

    protected boolean hasTime() {
        return hasTime(this.slimefunRecipe);
    }

    protected boolean hasSpeed() {
        return hasSpeed(this.slimefunCategory);
    }

    protected boolean hasOutputs() {
        return hasOutputs(this.slimefunRecipe);
    }
}
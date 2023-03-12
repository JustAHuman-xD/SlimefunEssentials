package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.compat.emi.EmiLabel;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProcessRecipe implements EmiRecipe {
    protected final EmiRecipeCategory category;
    protected final List<EmiIngredient> inputs;
    protected final List<EmiStack> outputs;
    protected final List<SlimefunLabel> labels;
    protected final Integer energy;
    protected final Integer time;
    protected final Integer speed;
    
    public ProcessRecipe(EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, List<SlimefunLabel> labels, Integer energy, Integer time, Integer speed) {
        this.category = category;
        this.inputs = inputs;
        this.outputs = outputs;
        this.labels = labels;
        this.energy = energy;
        this.time = time;
        this.speed = speed;
    }
    
    public int getWidgetsWidth() {
        return (hasLabels() ? EmiUtils.label * this.labels.size() + EmiUtils.padding * this.labels.size() : 0) + (hasEnergy() ? EmiUtils.chargeWidth + EmiUtils.padding : 0) + (hasInputs() ? EmiUtils.slot * this.inputs.size() + EmiUtils.padding * this.inputs.size() : EmiUtils.slot + EmiUtils.padding) + (EmiUtils.arrowWidth + EmiUtils.padding) + (hasOutputs() ? EmiUtils.bigSlot * this.outputs.size() + EmiUtils.padding * (this.outputs.size() - 1): 0);
    }
    
    public int getWidgetsHeight() {
        if (hasOutputs()) {
            return EmiUtils.bigSlot;
        } else {
            return EmiUtils.slot;
        }
    }
    
    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
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
    public int getDisplayWidth() {
        return getWidgetsWidth() + EmiUtils.padding * 2;
    }
    
    @Override
    public int getDisplayHeight() {
        return getWidgetsHeight() + EmiUtils.padding * 2;
    }
    
    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offsetYLabel = (getDisplayHeight() - EmiUtils.label) / 2;
        final int offsetYSlot = (getDisplayHeight() - EmiUtils.slot) / 2;
        final int offsetYCharge = (getDisplayHeight() - EmiUtils.chargeHeight) / 2;
        final int offsetYArrow = (getDisplayHeight() - EmiUtils.arrowHeight) / 2;
        final int offsetYBig = (getDisplayHeight() - EmiUtils.bigSlot) / 2;
        int offsetX = EmiUtils.padding;
        
        if (hasLabels()) {
            for (SlimefunLabel slimefunLabel : this.labels) {
                widgets.add(new EmiLabel(slimefunLabel, offsetX, offsetYLabel));
                offsetX += EmiUtils.label + EmiUtils.padding;
            }
        }
    
        // Display Energy
        if (hasEnergy() && hasOutputs()) {
            addEnergyDisplay(widgets, offsetX, offsetYCharge);
            offsetX += EmiUtils.chargeWidth + EmiUtils.padding;
        }
    
        //Display Inputs
        if (hasInputs()) {
            for (EmiIngredient input : this.inputs) {
                widgets.addSlot(input, offsetX, offsetYSlot);
                offsetX += EmiUtils.slot + EmiUtils.padding;
            }
        } else {
            widgets.addSlot((EmiIngredient) this.category.icon, offsetX, offsetYSlot);
            offsetX += EmiUtils.slot + EmiUtils.padding;
        }
    
        // Display Time
        if (hasTime()) {
            final int sfTicks = Math.max(1, this.time / 10 / (hasSpeed() ? this.speed : 1));
            final int millis =  sfTicks * 500;
            widgets.addFillingArrow(offsetX, offsetYArrow, millis).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(sfTicks / 2f), EmiUtils.numberFormat.format(sfTicks * 10))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetYArrow);
        }
        offsetX += EmiUtils.arrowWidth + EmiUtils.padding;
    
        // Display Outputs
        if (hasOutputs()) {
            for (EmiStack output : this.outputs) {
                widgets.addSlot(output, offsetX, offsetYBig).output(true);
                offsetX += EmiUtils.bigSlot + EmiUtils.padding;
            }
        } else if (hasEnergy()) {
            addEnergyDisplay(widgets, offsetX, offsetYCharge);
        }
    }
    
    protected void addEnergyDisplay(WidgetHolder widgets, int offsetX, int offsetYCharge) {
        final int totalEnergy = this.energy * Math.max(1, this.time / 10 / (hasSpeed() ? this.speed : 1));
        widgets.addTexture(EmiUtils.EMPTY_CHARGE, offsetX, offsetYCharge);
        widgets.addAnimatedTexture(totalEnergy >= 0 ? EmiUtils.GAIN_CHARGE : EmiUtils.LOOSE_CHARGE, offsetX, offsetYCharge, 1000, false, totalEnergy < 0, totalEnergy < 0).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.energy." + (totalEnergy >= 0 ? "generate" : "use"), EmiUtils.numberFormat.format(Math.abs(totalEnergy)))))));
    }
    
    protected boolean hasInputs() {
        return this.inputs != null && !this.inputs.isEmpty();
    }
    
    protected boolean hasOutputs() {
        return this.outputs != null && !this.outputs.isEmpty();
    }
    
    protected boolean hasLabels() {
        return this.labels != null && !this.labels.isEmpty();
    }
    
    protected boolean hasEnergy() {
        return this.energy != null;
    }
    
    protected boolean hasTime() {
        return this.time != null;
    }
    
    protected boolean hasSpeed() {
        return this.speed != null;
    }
}

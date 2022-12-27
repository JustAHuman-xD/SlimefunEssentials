package me.justahuman.slimefun_essentials.compatibility.emi.recipetype;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.compatibility.emi.EmiUtils;
import me.justahuman.slimefun_essentials.compatibility.emi.misc.EmiCondition;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProcessRecipe implements EmiRecipe {

    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final List<EmiCondition> conditions;
    private final int ticks;
    private final int energy;

    public ProcessRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs, int ticks, int energy, List<EmiCondition> conditions) {
        this.emiRecipeCategory = emiRecipeCategory;
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.ticks = ticks;
        this.energy = energy;
        this.conditions = conditions;
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
        int offsetX = (getDisplayWidth() - EmiUtils.conditionWidth * this.conditions.size() - EmiUtils.padding * this.conditions.size() - (hasEnergy() ? EmiUtils.chargeWidth + EmiUtils.padding : 0) - (hasInputs() ? EmiUtils.slotWidth * this.inputs.size() + EmiUtils.padding * this.inputs.size() : EmiUtils.slotWidth + EmiUtils.padding) - (EmiUtils.arrowWidth + EmiUtils.padding) - EmiUtils.bigSlotWidth * this.outputs.size()) /2;
        final int offsetYCondition = (getDisplayHeight() - EmiUtils.conditionHeight) / 2;
        final int offsetYCharge = (getDisplayHeight() - EmiUtils.chargeHeight) / 2;
        final int offsetYSlot = (getDisplayHeight() - EmiUtils.slotHeight) / 2;
        final int offsetYBig = (getDisplayHeight() - EmiUtils.bigSlotHeight) / 2;
        final int offsetYArrow = (getDisplayHeight() - EmiUtils.arrowHeight) / 2;
        final int time = (int) (this.ticks / 20f * 1000);
        
        //Display Conditions
        if (hasConditions()) {
            for (EmiCondition emiCondition : this.conditions) {
                final String conditionId = emiCondition.id();
                final EmiTexture emiTexture = emiCondition.texture();
                final List<TooltipComponent> tooltip = List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.condition." + conditionId))));
                widgets.addTexture(emiTexture, offsetX, offsetYCondition).tooltip((mx, my) -> tooltip);
                offsetX += EmiUtils.conditionWidth + EmiUtils.padding;
            }
        }
        
        //Display Energy
        if (hasEnergy() && hasOutputs()) {
            widgets.addTexture(EmiUtils.EMPTY_CHARGE, offsetX, offsetYCharge);
            widgets.addAnimatedTexture(this.energy > 0 ? EmiUtils.GAIN_CHARGE : EmiUtils.LOOSE_CHARGE, offsetX, offsetYCharge, 1000, false, this.energy <= 0, this.energy <= 0).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.energy." + (this.energy > 0 ? "generate" : "use"), EmiUtils.numberFormat.format(Math.abs(this.energy)))))));
            offsetX += EmiUtils.chargeWidth + EmiUtils.padding;
        }
        
        //Display Inputs
        if (hasInputs()) {
            for (EmiIngredient input : this.inputs) {
                widgets.addSlot(input, offsetX, offsetYSlot);
                offsetX += EmiUtils.slotWidth + EmiUtils.padding;
            }
        } else {
            widgets.addSlot((EmiIngredient) this.emiRecipeCategory.icon, offsetX, offsetYSlot);
            offsetX += EmiUtils.slotWidth + EmiUtils.padding;
        }
        
        //Display Time
        if (hasTime()) {
            widgets.addFillingArrow(offsetX, offsetYArrow, time).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.time", EmiUtils.numberFormat.format(ticks / 20f), EmiUtils.numberFormat.format(ticks))))));
        } else {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, offsetX, offsetYArrow);
        }
        offsetX += EmiUtils.arrowWidth + EmiUtils.padding;
        
        //Display Outputs
        if (hasOutputs()) {
            for (EmiStack output : this.outputs) {
                widgets.addSlot(output, offsetX, offsetYBig).output(true);
                offsetX += EmiUtils.bigSlotWidth + EmiUtils.padding;
            }
        } else if (hasEnergy()) {
            widgets.addTexture(EmiUtils.EMPTY_CHARGE, offsetX, offsetYCharge);
            widgets.addAnimatedTexture(this.energy > 0 ? EmiUtils.GAIN_CHARGE : EmiUtils.LOOSE_CHARGE, offsetX, offsetYCharge, 1000, false, this.energy <= 0, this.energy <= 0).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.energy." + (this.energy > 0 ? "generate" : "use"), EmiUtils.numberFormat.format(Math.abs(this.energy)))))));
        }
    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
    
    private boolean hasEnergy() {
        return this.energy != 0;
    }
    
    private boolean hasTime() {
        return this.ticks > 0;
    }
    
    private boolean hasInputs() {
        return !this.inputs.isEmpty();
    }
    
    private boolean hasOutputs() {
        return !this.outputs.isEmpty();
    }
    
    private boolean hasConditions() {
        return !this.conditions.isEmpty();
    }
}

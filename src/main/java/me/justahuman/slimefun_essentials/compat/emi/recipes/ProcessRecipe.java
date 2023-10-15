package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiIntegration;
import me.justahuman.slimefun_essentials.compat.emi.EmiLabel;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.compat.emi.ReverseFillingArrowWidget;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProcessRecipe extends RecipeRenderer implements EmiRecipe {
    protected final SlimefunCategory slimefunCategory;
    protected final SlimefunRecipe slimefunRecipe;
    protected final EmiRecipeCategory emiRecipeCategory;
    protected final List<EmiIngredient> inputs = new ArrayList<>();
    protected final List<EmiStack> outputs = new ArrayList<>();

    public ProcessRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        this(Type.PROCESS, slimefunCategory, slimefunRecipe, emiRecipeCategory);
    }

    public ProcessRecipe(Type type, SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory emiRecipeCategory) {
        super(type);

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
    public int getDisplayWidth() {
        return getDisplayWidth(this.slimefunRecipe);
    }

    @Override
    public int getDisplayHeight() {
        return getDisplayHeight(this.slimefunRecipe);
    }
    
    @Override
    public boolean supportsRecipeTree() {
        return true;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final OffsetBuilder offsets = new OffsetBuilder(this, this.slimefunRecipe);

        // Display Labels
        if (this.slimefunRecipe.hasLabels()) {
            for (SlimefunLabel slimefunLabel : this.slimefunRecipe.labels()) {
                widgets.add(new EmiLabel(slimefunLabel, offsets.getX(), offsets.label()));
                offsets.x().addLabel();
            }
        }
    
        // Display Energy
        addEnergyWithCheck(widgets, offsets);
    
        //Display Inputs
        if (this.slimefunRecipe.hasInputs()) {
            for (EmiIngredient input : this.inputs) {
                widgets.addSlot(input, offsets.getX(), offsets.slot());
                offsets.x().addSlot();
            }
        } else {
            widgets.addSlot((EmiIngredient) this.emiRecipeCategory.icon, offsets.getX(), offsets.slot());
            offsets.x().addSlot();
        }
    
        // Display Arrow
        addArrowWithCheck(widgets, offsets);
    
        // Display Outputs
        addOutputsOrEnergy(widgets, offsets);
    }

    protected void addEnergyWithCheck(WidgetHolder widgets, OffsetBuilder offsets) {
        if (this.slimefunRecipe.hasEnergy() && this.slimefunRecipe.hasOutputs()) {
            addEnergy(widgets, offsets);
        }
    }

    protected void addEnergy(WidgetHolder widgets, OffsetBuilder offsets) {
        addEnergy(widgets, offsets.getX(), offsets.energy());
        offsets.x().addEnergy();
    }

    protected void addEnergy(WidgetHolder widgets, int x, int y) {
        final int totalEnergy = this.slimefunRecipe.energy() * Math.max(1, this.slimefunRecipe.time() / 10 / (this.slimefunCategory.hasSpeed() ? this.slimefunCategory.speed() : 1));
        widgets.addTexture(EmiUtils.EMPTY_CHARGE, x, y);
        widgets.addAnimatedTexture(totalEnergy >= 0 ? EmiUtils.GAIN_CHARGE : EmiUtils.LOOSE_CHARGE, x, y, 1000, false, totalEnergy < 0, totalEnergy < 0).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipe.energy." + (totalEnergy >= 0 ? "generate" : "use"), TextureUtils.numberFormat.format(Math.abs(totalEnergy)))))));
    }

    protected void addArrowWithCheck(WidgetHolder widgets, OffsetBuilder offsets) {
        addArrowWithCheck(widgets, offsets.getX(), offsets.arrow(), false);
        offsets.x().addArrow();
    }

    protected void addArrowWithCheck(WidgetHolder widgets, int x, int y, boolean backwards) {
        if (this.slimefunRecipe.hasTime()) {
            final int sfTicks = Math.max(1, this.slimefunRecipe.time() / 10 / (this.slimefunCategory.hasSpeed() ? this.slimefunCategory.speed() : 1));
            final int millis =  sfTicks * 500;
            addFillingArrow(widgets, x, y, backwards, sfTicks, millis);
        } else {
            addArrow(widgets, x, y, backwards);
        }
    }

    protected void addArrow(WidgetHolder widgets, int x, int y, boolean backwards) {
        widgets.addTexture(backwards ? EmiUtils.BACKWARDS_EMPTY_ARROW : EmiTexture.EMPTY_ARROW, x, y);
    }

    protected void addFillingArrow(WidgetHolder widgets, int x, int y, boolean backwards, int sfTicks, int millis) {
        widgets.add(backwards ? new ReverseFillingArrowWidget(x, y, millis) : new FillingArrowWidget(x, y, millis)).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("slimefun_essentials.recipes.time", TextureUtils.numberFormat.format(sfTicks / 2f), TextureUtils.numberFormat.format(sfTicks * 10L))))));
    }

    protected void addOutputsOrEnergy(WidgetHolder widgets, OffsetBuilder offsets) {
        if (this.slimefunRecipe.hasOutputs()) {
            addOutputs(widgets, offsets);
        } else if (this.slimefunRecipe.hasEnergy()) {
            addEnergy(widgets, offsets);
        }
    }

    protected void addOutputs(WidgetHolder widgets, OffsetBuilder offsets) {
        for (EmiStack output : this.outputs) {
            widgets.addSlot(output, offsets.getX(), offsets.output()).large(true);
            offsets.x().addOutput();
        }
    }
}
package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.ReiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ProcessDisplay implements Display, RecipeRenderer {
    protected final SlimefunCategory slimefunCategory;
    protected final SlimefunRecipe slimefunRecipe;
    protected final List<EntryIngredient> inputs = new ArrayList<>();
    protected final List<EntryIngredient> outputs = new ArrayList<>();
    
    public ProcessDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        this.slimefunCategory = slimefunCategory;
        this.slimefunRecipe = slimefunRecipe;
        this.inputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getInputEntries(this.slimefunRecipe));
        this.outputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getOutputEntries(this.slimefunRecipe));
    }

    public List<Widget> setupDisplay(Rectangle bounds, ItemStack icon) {
        final List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createCategoryBase(bounds));

        final int labelOffset = bounds.getMinY() + calculateYOffset(TextureUtils.labelSize);
        final int energyOffset = bounds.getMinY() + calculateYOffset(TextureUtils.energyHeight);
        final int slotOffset = bounds.getMinY() + calculateYOffset(TextureUtils.slotSize);
        final int arrowOffset = bounds.getMinY() + calculateYOffset(TextureUtils.arrowHeight);
        final int outputOffset = bounds.getMinY() + calculateYOffset(TextureUtils.outputSize);
        int xOffset = bounds.getMinX() + calculateXOffset();

        // Display Labels
        if (hasLabels()) {
            for (SlimefunLabel slimefunLabel : this.slimefunRecipe.labels()) {
                widgets.add(ReiIntegration.widgetFromSlimefunLabel(slimefunLabel, xOffset, labelOffset));
                xOffset += TextureUtils.labelSize + TextureUtils.padding;
            }
        }

        // Display Energy
        if (hasEnergy() && hasOutputs()) {
            widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.ENERGY, xOffset, energyOffset));
            xOffset += TextureUtils.energyWidth + TextureUtils.padding;
        }

        // Display Inputs
        if (hasInputs()) {
            for (EntryIngredient entryIngredient : getInputEntries()) {
                widgets.add(Widgets.createSlot(new Point(xOffset + 1, slotOffset + 1)).entries(entryIngredient).markInput());
                xOffset += TextureUtils.slotSize + TextureUtils.padding;
            }
        } else {
            widgets.add(Widgets.createSlot(new Point(xOffset + 1, slotOffset + 1)).entry(EntryStacks.of(icon)).markInput());
            xOffset += TextureUtils.slotSize + TextureUtils.padding;
        }

        // Display Time
        // TODO: Support Animated Textures
        widgets.add(Widgets.createArrow(new Point(xOffset, arrowOffset)));
        xOffset += TextureUtils.arrowWidth + TextureUtils.padding;

        // Display Outputs
        if (hasOutputs()) {
            for (EntryIngredient entryIngredient : getOutputEntries()) {
                widgets.add(Widgets.createSlot(new Point(xOffset + 5, outputOffset + 5)).entries(entryIngredient).disableBackground().markOutput());
                widgets.add(Widgets.createResultSlotBackground(new Point(xOffset + 5, outputOffset + 5)));
                xOffset += TextureUtils.outputSize + TextureUtils.padding;
            }
        } else if (hasEnergy()) {
            widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.ENERGY, xOffset, energyOffset));
        }

        return widgets;
    }

    @Override
    public int getContentsWidth() {
        return TextureUtils.getContentsWidth(this.slimefunRecipe);
    }

    @Override
    public int getDisplayWidth() {
        return getContentsWidth() + TextureUtils.padding * 4;
    }

    @Override
    public int getContentsHeight() {
        return TextureUtils.getContentsHeight(this.slimefunCategory);
    }

    @Override
    public int getDisplayHeight() {
        return getContentsHeight() + TextureUtils.padding * 4;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }
    
    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputs;
    }
    
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.newIdentifier(this.slimefunCategory.id().toLowerCase()));
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

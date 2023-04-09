package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.ReiIntegration;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class SlimefunDisplay extends RecipeRenderer implements Display {
    protected final SlimefunCategory slimefunCategory;
    protected final SlimefunRecipe slimefunRecipe;
    protected final List<EntryIngredient> inputs = new ArrayList<>();
    protected final List<EntryIngredient> outputs = new ArrayList<>();

    protected SlimefunDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        this(Type.PROCESS, slimefunCategory, slimefunRecipe);
    }

    protected SlimefunDisplay(Type type, SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        super(type);

        this.slimefunCategory = slimefunCategory;
        this.slimefunRecipe = slimefunRecipe;
        this.inputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getInputEntries(this.slimefunRecipe));
        this.outputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getOutputEntries(this.slimefunRecipe));
    }

    public SlimefunRecipe slimefunRecipe() {
        return this.slimefunRecipe;
    }

    public abstract List<Widget> setupDisplay(OffsetBuilder offsets, List<Widget> widgets, Rectangle bounds, ItemStack icon);
}

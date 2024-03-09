package me.justahuman.slimefun_essentials.compat.jei.categories;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import me.justahuman.slimefun_essentials.compat.jei.JeiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class GridCategory extends ProcessCategory {
    protected final int side;
    public GridCategory(IGuiHelper guiHelper, SlimefunCategory slimefunCategory, ItemStack catalyst, int side) {
        super(Type.grid(side), guiHelper, slimefunCategory, catalyst);

        this.side = side;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, calculateXOffset(recipe), TextureUtils.PADDING);
        recipe.fillInputs(this.side * this.side);

        if (recipe.hasEnergy() && recipe.hasOutputs()) {
            offsets.x().addEnergy();
        }

        int i = 0;
        for (int y = 1; y <= this.side; y++) {
            for (int x = 1; x <= this.side; x++) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(i));
                offsets.x().addSlot(false);
                i++;
            }
            offsets.x().subtract(TextureUtils.SLOT_SIZE * this.side);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.SLOT_SIZE * this.side).addPadding();

        offsets.x().addArrow();

        if (recipe.hasOutputs()) {
            for (SlimefunRecipeComponent component : recipe.outputs()) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.OUTPUT, offsets.getX() + 5, offsets.output() + 5), component);
            }
        }
    }

    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext graphics, double mouseX, double mouseY) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, calculateXOffset(recipe), TextureUtils.PADDING);
        recipe.fillInputs(this.side * this.side);

        // Display Energy
        addEnergyWithCheck(graphics, offsets, recipe);

        for (int y = 1; y <= this.side; y++) {
            for (int x = 1; x <= this.side; x++) {
                TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
                offsets.x().addSlot(false);
            }
            offsets.x().subtract(TextureUtils.SLOT_SIZE * this.side);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.SLOT_SIZE * this.side).addPadding();

        // Display Arrow
        addArrow(graphics, offsets, recipe);

        // Display Outputs
        addOutputsOrEnergy(graphics, offsets, recipe);
    }
}

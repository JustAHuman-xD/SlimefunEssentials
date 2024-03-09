package me.justahuman.slimefun_essentials.compat.jei.categories;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.jei.JeiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ReactorCategory extends ProcessCategory {
    public ReactorCategory(IGuiHelper guiHelper, SlimefunCategory slimefunCategory, ItemStack catalyst) {
        super(Type.REACTOR, guiHelper, slimefunCategory, catalyst);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, calculateXOffset(this.slimefunCategory, recipe), calculateYOffset(this.slimefunCategory, recipe));
        recipe.fillInputs(4);

        JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(0));
        offsets.y().addSlot(false);
        JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(0));
        offsets.y().addSlot(false);
        JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(0));
        offsets.x().addSlot();

        offsets.x().addArrow();

        if (recipe.hasOutputs()) {
            JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.OUTPUT, offsets.getX() + 5, offsets.getY() + 5), recipe.outputs().get(0));
        }

        if (recipe.hasEnergy()) {
            offsets.x().add(recipe.hasOutputs() ? TextureUtils.OUTPUT_SIZE : TextureUtils.ENERGY_WIDTH).addPadding();
        }

        offsets.x().addArrow();
        offsets.y().subtract(TextureUtils.SLOT_SIZE * 2);

        JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(1));
        offsets.y().addSlot(false);
        JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(2));
        offsets.y().addSlot(false);
        JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(3));
    }

    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext graphics, double mouseX, double mouseY) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, calculateXOffset(this.slimefunCategory, recipe), calculateYOffset(this.slimefunCategory, recipe));
        recipe.fillInputs(4);

        TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
        offsets.x().addSlot();

        addFillingArrow(graphics, offsets.getX(), offsets.getY(), false, getTime(recipe));
        offsets.x().addArrow();

        if (recipe.hasOutputs()) {
            TextureUtils.OUTPUT.draw(graphics, offsets.getX(), offsets.getY());
        }

        if (recipe.hasEnergy()) {
            addEnergy(graphics, offsets.getX() + (recipe.hasOutputs() ? (TextureUtils.OUTPUT_SIZE - TextureUtils.ENERGY_WIDTH) / 2 : 0), offsets.getY() + (recipe.hasOutputs() ? - TextureUtils.ENERGY_HEIGHT - TextureUtils.PADDING : TextureUtils.PADDING), recipe.energy() < 0);
            offsets.x().add(recipe.hasOutputs() ? TextureUtils.OUTPUT_SIZE : TextureUtils.ENERGY_WIDTH).addPadding();
        }

        addFillingArrow(graphics, offsets.getX(), offsets.getY(), true, getTime(recipe));
        offsets.x().addArrow();
        offsets.y().subtract(TextureUtils.SLOT_SIZE * 2);
        TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
    }
}

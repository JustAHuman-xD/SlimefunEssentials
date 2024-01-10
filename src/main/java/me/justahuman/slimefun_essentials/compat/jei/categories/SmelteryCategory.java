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
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SmelteryCategory extends ProcessCategory {
    public SmelteryCategory(IGuiHelper guiHelper, SlimefunCategory slimefunCategory, ItemStack catalyst) {
        super(Type.SMELTERY, guiHelper, slimefunCategory, catalyst);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, TextureUtils.PADDING, TextureUtils.PADDING);
        recipe.fillInputs(6);
        recipe.fillOutputs(1);

        if (recipe.hasEnergy() && recipe.hasOutputs()) {
            offsets.x().addEnergy();
        }

        int i = 0;
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 2; x++) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.getY() + 1), recipe.inputs().get(i));
                offsets.x().addSlot(false);
                i++;
            }
            offsets.x().subtract(TextureUtils.SLOT_SIZE * 2);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.SLOT_SIZE * 2).addPadding();

        offsets.x().addArrow();

        if (recipe.hasOutputs()) {
            for (SlimefunRecipeComponent output : recipe.outputs()) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.OUTPUT, offsets.getX() + 5, offsets.output() + 5), output);
                offsets.x().addOutput();
            }
        }
    }

    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext graphics, double mouseX, double mouseY) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, TextureUtils.PADDING, TextureUtils.PADDING);
        recipe.fillInputs(6);
        recipe.fillOutputs(1);

        // Display Energy
        addEnergyWithCheck(graphics, offsets, recipe);

        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 2; x++) {
                TextureUtils.SLOT.draw(graphics, offsets.getX(), offsets.getY());
                offsets.x().addSlot(false);
            }
            offsets.x().subtract(TextureUtils.SLOT_SIZE * 2);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.SLOT_SIZE * 2).addPadding();

        // Display Arrow
        addArrow(graphics, offsets, recipe);

        // Display Outputs
        addOutputs(graphics, offsets, recipe);
    }

    @NotNull
    @Override
    public List<Text> getTooltipStrings(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final List<Text> tooltips = new ArrayList<>();
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, TextureUtils.PADDING, TextureUtils.PADDING);

        if (recipe.hasEnergy() && recipe.hasOutputs()) {
            if (tooltipActive(mouseX, mouseY, offsets.getX(), offsets.energy(), TextureUtils.ENERGY)) {
                tooltips.add(energyTooltip(recipe));
            }
            offsets.x().addEnergy();
        }

        offsets.x().addSlot(false).addSlot(false).addPadding();
        offsets.y().addSlot(false).addSlot(false).addSlot(false);

        if (tooltipActive(mouseX, mouseY, offsets.getX(), offsets.arrow(), TextureUtils.ARROW)) {
            tooltips.add(timeTooltip(recipe));
        }
        return tooltips;
    }
}

package me.justahuman.slimefun_essentials.compat.jei.categories;

import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import me.justahuman.slimefun_essentials.compat.jei.JeiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ProcessCategory implements IRecipeCategory<SlimefunRecipe>, RecipeRenderer {
    protected final IGuiHelper guiHelper;
    protected final SlimefunCategory slimefunCategory;
    protected final ItemStack catalyst;
    protected final IDrawable icon;
    protected final IDrawable background;
    
    public ProcessCategory(IGuiHelper guiHelper, SlimefunCategory slimefunCategory, ItemStack catalyst) {
        this.guiHelper = guiHelper;
        this.slimefunCategory = slimefunCategory;
        this.catalyst = catalyst;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, catalyst);
        this.background = guiHelper.drawableBuilder(TextureUtils.WIDGETS, 0, 0, 0, 0).addPadding(yPadding(), yPadding(), xPadding(), xPadding()).build();
    }
    
    public int xPadding() {
        return getDisplayWidth() / 2;
    }
    
    public int yPadding() {
        return getDisplayHeight() / 2;
    }

    @Override
    public int getContentsWidth() {
        return TextureUtils.getContentsWidth(this.slimefunCategory);
    }

    @Override
    public int getContentsHeight() {
        return TextureUtils.getContentsHeight(this.slimefunCategory);
    }
    
    @Override
    @NotNull
    public RecipeType<SlimefunRecipe> getRecipeType() {
        return RecipeType.create(Utils.ID, this.slimefunCategory.id().toLowerCase(), SlimefunRecipe.class);
    }
    
    @Override
    @NotNull
    public Text getTitle() {
        return Text.translatable("emi.category.slimefun", this.catalyst.getName());
    }
    
    @Override
    @NotNull
    public IDrawable getBackground() {
        return this.background;
    }
    
    @Override
    @NotNull
    public IDrawable getIcon() {
        return this.icon;
    }
    
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        final int slotOffset = calculateYOffset(TextureUtils.slotSize);
        final int outputOffset = calculateYOffset(TextureUtils.outputSize);
        int xOffset = (getDisplayWidth() - TextureUtils.getContentsWidth(recipe)) / 2;
    
        if (hasLabels(recipe)) {
            xOffset += (TextureUtils.labelSize + TextureUtils.padding) * recipe.labels().size();
        }

        if (hasEnergy(recipe) && hasOutputs(recipe)) {
            xOffset += TextureUtils.energyWidth + TextureUtils.padding;
        }

        if (hasInputs(recipe)) {
            for (SlimefunRecipeComponent input : recipe.inputs()) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, xOffset + 1, slotOffset + 1), input);
                xOffset += TextureUtils.slotSize + TextureUtils.padding;
            }
        } else {
            JeiIntegration.RECIPE_INTERPRETER.addIngredient(builder.addSlot(RecipeIngredientRole.INPUT, xOffset + 1, slotOffset + 1), this.catalyst);
            xOffset += TextureUtils.slotSize + TextureUtils.padding;
        }

        xOffset += TextureUtils.arrowWidth + TextureUtils.padding;

        if (hasOutputs(recipe)) {
            for (SlimefunRecipeComponent output : recipe.outputs()) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.OUTPUT, xOffset + 5, outputOffset + 5), output);
                xOffset += TextureUtils.outputSize + TextureUtils.padding;
            }
        }
    }
    
    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
        final int labelOffset = calculateYOffset(TextureUtils.labelSize);
        final int energyOffset = calculateYOffset(TextureUtils.energyHeight);
        final int slotOffset = calculateYOffset(TextureUtils.slotSize);
        final int arrowOffset = calculateYOffset(TextureUtils.arrowHeight);
        final int outputOffset = calculateYOffset(TextureUtils.outputSize);
        int xOffset = calculateXOffset();

        // Display Labels
        if (hasLabels(recipe)) {
            for (SlimefunLabel slimefunLabel : recipe.labels()) {
                slimefunLabel.draw(stack, xOffset, labelOffset);
                xOffset += TextureUtils.labelSize + TextureUtils.padding;
            }
        }

        // Display Energy
        if (hasEnergy(recipe) && hasOutputs(recipe)) {
            TextureUtils.ENERGY.draw(stack, xOffset, energyOffset);
            xOffset += TextureUtils.energyWidth + TextureUtils.padding;
        }

        // Display Inputs, only the slot icon
        if (hasInputs(recipe)) {
            for (SlimefunRecipeComponent ignored : recipe.inputs()) {
                TextureUtils.SLOT.draw(stack, xOffset, slotOffset);
                xOffset += TextureUtils.slotSize + TextureUtils.padding;
            }
        } else {
            TextureUtils.SLOT.draw(stack, xOffset, slotOffset);
            xOffset += TextureUtils.slotSize + TextureUtils.padding;
        }

        // Display Time
        // TODO: Support Animated Textures
        TextureUtils.ARROW.draw(stack, xOffset, arrowOffset);
        xOffset += TextureUtils.arrowWidth + TextureUtils.padding;

        // Display Outputs
        if (hasOutputs(recipe)) {
            for (SlimefunRecipeComponent ignored : recipe.outputs()) {
                TextureUtils.OUTPUT.draw(stack, xOffset, outputOffset);
                xOffset += TextureUtils.outputSize + TextureUtils.padding;
            }
        } else if (hasEnergy(recipe)) {
            TextureUtils.ENERGY.draw(stack, xOffset, energyOffset);
        }
    }
}

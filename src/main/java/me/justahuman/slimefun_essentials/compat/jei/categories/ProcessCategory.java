package me.justahuman.slimefun_essentials.compat.jei.categories;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
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

public class ProcessCategory extends RecipeRenderer implements IRecipeCategory<SlimefunRecipe> {
    protected final IGuiHelper guiHelper;
    protected final SlimefunCategory slimefunCategory;
    protected final ItemStack catalyst;
    protected final IDrawable icon;
    protected final IDrawable background;

    public ProcessCategory(IGuiHelper guiHelper, SlimefunCategory slimefunCategory, ItemStack catalyst) {
        this(Type.PROCESS, guiHelper, slimefunCategory, catalyst);
    }
    
    public ProcessCategory(Type type, IGuiHelper guiHelper, SlimefunCategory slimefunCategory, ItemStack catalyst) {
        super(type);

        this.guiHelper = guiHelper;
        this.slimefunCategory = slimefunCategory;
        this.catalyst = catalyst;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, catalyst);
        this.background = guiHelper.drawableBuilder(TextureUtils.WIDGETS, 0, 0, 0, 0).addPadding(yPadding(), yPadding(), xPadding(), xPadding()).build();
    }
    
    public int xPadding() {
        return getDisplayWidth(this.slimefunCategory) / 2;
    }
    
    public int yPadding() {
        return getDisplayHeight(this.slimefunCategory) / 2;
    }

    @Override
    @NotNull
    public RecipeType<SlimefunRecipe> getRecipeType() {
        return RecipeType.create(Utils.ID, this.slimefunCategory.id().toLowerCase(), SlimefunRecipe.class);
    }
    
    @Override
    @NotNull
    public Text getTitle() {
        return Text.translatable("slimefun_essentials.recipe.category.slimefun", this.catalyst.getName());
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
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe);
    
        if (recipe.hasLabels()) {
            offsets.x().add((TextureUtils.labelSize + TextureUtils.padding) * recipe.labels().size());
        }

        if (recipe.hasEnergy() && recipe.hasOutputs()) {
            offsets.x().addEnergy();
        }

        if (recipe.hasInputs()) {
            for (SlimefunRecipeComponent input : recipe.inputs()) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.slot() + 1), input);
                offsets.x().addSlot();
            }
        } else {
            JeiIntegration.RECIPE_INTERPRETER.addIngredient(builder.addSlot(RecipeIngredientRole.INPUT, offsets.getX() + 1, offsets.slot() + 1), this.catalyst);
            offsets.x().addSlot();
        }

        offsets.x().addArrow();

        if (recipe.hasOutputs()) {
            for (SlimefunRecipeComponent output : recipe.outputs()) {
                JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addSlot(RecipeIngredientRole.OUTPUT, offsets.getX() + 5, offsets.output() + 5), output);
                offsets.x().addOutput();
            }
        }
    }
    
    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe);

        // Display Labels
        if (recipe.hasLabels()) {
            for (SlimefunLabel slimefunLabel : recipe.labels()) {
                slimefunLabel.draw(stack, offsets.getX(), offsets.label());
                offsets.x().addLabel();
            }
        }

        // Display Energy
        addEnergyWithCheck(stack, offsets, recipe);

        // Display Inputs, only the slot icon
        if (recipe.hasInputs()) {
            for (SlimefunRecipeComponent ignored : recipe.inputs()) {
                TextureUtils.SLOT.draw(stack, offsets.getX(), offsets.slot());
                offsets.x().addSlot();
            }
        } else {
            TextureUtils.SLOT.draw(stack, offsets.getX(), offsets.slot());
            offsets.x().addSlot();
        }

        // Display Arrow
        addArrow(stack, offsets, recipe);

        // Display Outputs
        addOutputsOrEnergy(stack, offsets, recipe);
    }

    protected void addEnergyWithCheck(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        if (recipe.hasEnergy() && recipe.hasOutputs()) {
            addEnergy(stack, offsets);
        }
    }

    protected void addEnergy(MatrixStack stack, OffsetBuilder offsets) {
        addEnergy(stack, offsets.getX(), offsets.energy());
        offsets.x().addEnergy();
    }

    protected void addEnergy(MatrixStack stack, int x, int y) {
        TextureUtils.ENERGY.draw(stack, x, y);
    }

    protected void addArrow(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        addArrow(stack, recipe, offsets.getX(), offsets.arrow(),false);
        offsets.x().addArrow();
    }

    protected void addArrow(MatrixStack stack, SlimefunRecipe recipe, int x, int y, boolean backwards) {
        if (recipe.hasTime() && false) {
            final int sfTicks = Math.max(1, recipe.time() / 10 / (this.slimefunCategory.hasSpeed() ? this.slimefunCategory.speed() : 1));
            final int millis =  sfTicks * 500;
            addFillingArrow(stack, x, y, backwards, 0, 0);
        } else {
            addArrow(stack, x, y, backwards);
        }
    }

    protected void addArrow(MatrixStack stack, int x, int y, boolean backwards) {
        (backwards ? TextureUtils.BACKWARDS_ARROW : TextureUtils.ARROW).draw(stack, x, y);
    }

    protected void addFillingArrow(MatrixStack stack, int x, int y, boolean backwards, int sfTicks, int millis) {
        // TODO ANIMATED TEXTURES
    }

    protected void addOutputsOrEnergy(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        if (recipe.hasOutputs()) {
            addOutputs(stack, offsets, recipe);
        } else {
            addEnergy(stack, offsets);
        }
    }

    protected void addOutputs(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        for (SlimefunRecipeComponent ignored : recipe.outputs()) {
            TextureUtils.OUTPUT.draw(stack, offsets.getX(), offsets.output());
            offsets.x().addOutput();
        }
    }
}

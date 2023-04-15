package me.justahuman.slimefun_essentials.compat.jei.categories;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
    protected final IDrawableAnimated positiveEnergy;
    protected final IDrawableAnimated negativeEnergy;
    protected final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
    protected final LoadingCache<Integer, IDrawableAnimated> cachedBackwardsArrows;

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
        this.positiveEnergy = guiHelper.drawableBuilder(TextureUtils.WIDGETS, TextureUtils.ENERGY_POSITIVE.u(), TextureUtils.ENERGY.v(), TextureUtils.energyWidth, TextureUtils.energyHeight).buildAnimated(20, IDrawableAnimated.StartDirection.TOP, false);
        this.negativeEnergy = guiHelper.drawableBuilder(TextureUtils.WIDGETS, TextureUtils.ENERGY_NEGATIVE.u(), TextureUtils.ENERGY.v(), TextureUtils.energyWidth, TextureUtils.energyHeight).buildAnimated(20, IDrawableAnimated.StartDirection.BOTTOM, true);
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    @NotNull
                    public IDrawableAnimated load(@NotNull Integer sfTicks) {
                        return guiHelper.drawableBuilder(TextureUtils.WIDGETS, TextureUtils.ARROW.u(), TextureUtils.ARROW.v() + TextureUtils.arrowHeight, TextureUtils.arrowWidth, TextureUtils.arrowHeight).buildAnimated(sfTicks * 10, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
        this.cachedBackwardsArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    @NotNull
                    public IDrawableAnimated load(@NotNull Integer sfTicks) {
                        return guiHelper.drawableBuilder(TextureUtils.WIDGETS, TextureUtils.BACKWARDS_ARROW.u(), TextureUtils.BACKWARDS_ARROW.v() + TextureUtils.arrowHeight, TextureUtils.arrowWidth, TextureUtils.arrowHeight).buildAnimated(sfTicks * 10, IDrawableAnimated.StartDirection.RIGHT, false);
                    }
                });
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
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, calculateXOffset(this.slimefunCategory, recipe));

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
        final OffsetBuilder offsets = new OffsetBuilder(this, recipe, calculateXOffset(this.slimefunCategory, recipe));

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
            addEnergy(stack, offsets, recipe.energy() < 0);
        }
    }

    protected void addEnergy(MatrixStack stack, OffsetBuilder offsets, boolean negative) {
        addEnergy(stack, offsets.getX(), offsets.energy(), negative);
        offsets.x().addEnergy();
    }

    protected void addEnergy(MatrixStack stack, int x, int y, boolean negative) {
        TextureUtils.ENERGY.draw(stack, x, y);
        (negative ? this.negativeEnergy : this.positiveEnergy).draw(stack, x, y);
    }

    protected void addArrow(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        addArrow(stack, recipe, offsets.getX(), offsets.arrow(),false);
        offsets.x().addArrow();
    }

    protected void addArrow(MatrixStack stack, SlimefunRecipe recipe, int x, int y, boolean backwards) {
        if (recipe.hasTime()) {
            addFillingArrow(stack, x, y, backwards, getTime(recipe));
        } else {
            addArrow(stack, x, y, backwards);
        }
    }

    protected void addArrow(MatrixStack stack, int x, int y, boolean backwards) {
        (backwards ? TextureUtils.BACKWARDS_ARROW : TextureUtils.ARROW).draw(stack, x, y);
    }

    protected void addFillingArrow(MatrixStack stack, int x, int y, boolean backwards, int sfTicks) {
        addArrow(stack, x, y, backwards);
        (backwards ? this.cachedBackwardsArrows.getUnchecked(sfTicks) : this.cachedArrows.getUnchecked(sfTicks)).draw(stack, x, y);
    }

    protected void addOutputsOrEnergy(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        if (recipe.hasOutputs()) {
            addOutputs(stack, offsets, recipe);
        } else {
            addEnergy(stack, offsets, recipe.energy() < 0);
        }
    }

    protected void addOutputs(MatrixStack stack, OffsetBuilder offsets, SlimefunRecipe recipe) {
        for (SlimefunRecipeComponent ignored : recipe.outputs()) {
            TextureUtils.OUTPUT.draw(stack, offsets.getX(), offsets.output());
            offsets.x().addOutput();
        }
    }

    protected int getTime(SlimefunRecipe slimefunRecipe) {
        if (slimefunRecipe.hasTime()) {
            return slimefunRecipe.sfTicks(this.slimefunCategory.hasSpeed() ? this.slimefunCategory.speed() : 1);
        } else {
            return 2;
        }
    }
}

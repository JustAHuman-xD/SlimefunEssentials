package me.justahuman.slimefun_essentials.compat.jei.categories;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import me.justahuman.slimefun_essentials.compat.jei.JeiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProcessCategory implements IRecipeCategory<SlimefunRecipe> {
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
        this.background = guiHelper.drawableBuilder(Utils.WIDGETS, 0, 0, 0, 0).addPadding(getContentsHeight() / 2, getContentsHeight() / 2, getContentsWidth() / 2, getContentsWidth() / 2).build();
    }
    
    public int getContentsWidth() {
        int width = 0;
        for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
            width = Math.max(width, TextureUtils.getProcessWidth(slimefunRecipe.labels(), slimefunRecipe.inputs(), slimefunRecipe.outputs(), slimefunRecipe.energy()));
        }
        return width;
    }
    
    public int getContentsHeight() {
        for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
            if (!slimefunRecipe.outputs().isEmpty()) {
                return TextureUtils.bigSlot;
            }
        }
        return TextureUtils.slot;
    }
    
    @Override
    @NotNull
    public RecipeType<SlimefunRecipe> getRecipeType() {
        return RecipeType.create(Utils.ID, slimefunCategory.id().toLowerCase(), SlimefunRecipe.class);
    }
    
    @Override
    @NotNull
    public Text getTitle() {
        return Text.translatable("emi.category.slimefun", catalyst.getName());
    }
    
    @Override
    @NotNull
    public IDrawable getBackground() {
        return background;
    }
    
    @Override
    @NotNull
    public IDrawable getIcon() {
        return icon;
    }
    
    @Override
    @NotNull
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        int x = 0;
        for (SlimefunRecipeComponent input : recipe.inputs()) {
            if (input.getMultiId() != null) {
                List<ItemStack> items = new ArrayList<>();
                List<SlimefunItemStack> slimefunItems = new ArrayList<>();
                
                for (String id : input.getMultiId()) {
                    final Object stack = stackFromId(id);
                    if (stack instanceof ItemStack itemStack) {
                        items.add(itemStack);
                    } else if (stack instanceof SlimefunItemStack slimefunItemStack) {
                        slimefunItems.add(slimefunItemStack);
                    }
                }
                
                builder.addSlot(RecipeIngredientRole.INPUT, x, 0)
                        .addItemStacks(items)
                        .addIngredients(JeiIntegration.SLIMEFUN, slimefunItems);
            } else {
                final Object stack = stackFromId(input.getId());
                final IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, 0);
                if (stack instanceof ItemStack itemStack) {
                    slotBuilder.addItemStack(itemStack);
                } else if (stack instanceof SlimefunItemStack slimefunItemStack) {
                    slotBuilder.addIngredient(JeiIntegration.SLIMEFUN, slimefunItemStack);
                }
            }
            x++;
        }
        
        x = 0;
        for (SlimefunRecipeComponent output : recipe.outputs()) {
            if (output.getMultiId() != null) {
                List<ItemStack> items = new ArrayList<>();
                List<SlimefunItemStack> slimefunItems = new ArrayList<>();
        
                for (String id : output.getMultiId()) {
                    final Object stack = stackFromId(id);
                    if (stack instanceof ItemStack itemStack) {
                        items.add(itemStack);
                    } else if (stack instanceof SlimefunItemStack slimefunItemStack) {
                        slimefunItems.add(slimefunItemStack);
                    }
                }
        
                builder.addSlot(RecipeIngredientRole.OUTPUT, x, 0)
                        .addItemStacks(items)
                        .addIngredients(JeiIntegration.SLIMEFUN, slimefunItems);
            } else {
                final Object stack = stackFromId(output.getId());
                final IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, 0);
                if (stack instanceof ItemStack itemStack) {
                    slotBuilder.addItemStack(itemStack);
                } else if (stack instanceof SlimefunItemStack slimefunItemStack) {
                    slotBuilder.addIngredient(JeiIntegration.SLIMEFUN, slimefunItemStack);
                }
            }
            x++;
        }
    }
    
    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
    
    }
    
    public Object stackFromId(String id) {
        if (id.equals("")) {
            return ItemStack.EMPTY;
        }
        
        if (!id.contains(":")) {
            Utils.warn("Invalid JeiIngredient Id: " + id);
            return ItemStack.EMPTY;
        }
    
        final String type = id.substring(0, id.indexOf(":"));
        final String value = id.substring(id.indexOf(":") + 1);
        int amount;
        try {
            amount = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            amount = 1;
        }
    
        if (ResourceLoader.getSlimefunItems().containsKey(type)) {
            return ResourceLoader.getSlimefunItems().get(type).copy().setAmount(amount);
        }
    
        if (type.equals("entity")) {
            final Identifier identifier = new Identifier("minecraft:" + value);
            if (! Registries.ENTITY_TYPE.containsId(identifier)) {
                Utils.warn("Invalid JeiIngredient Entity Id: " + id);
                return ItemStack.EMPTY;
            }
            // TODO: Entity Support
            return ItemStack.EMPTY;
        } else if (type.equals("fluid")) {
            final Identifier identifier = new Identifier("minecraft:" + value);
            if (!Registries.FLUID.containsId(identifier)) {
                Utils.warn("Invalid JeiIngredient Fluid Id: " + id);
                return ItemStack.EMPTY;
            }
            // TODO: Fluid Support
            return ItemStack.EMPTY;
        } else if (type.contains("#")) {
            final Identifier identifier = new Identifier("minecraft:" + type.substring(1));
            // TODO: Tag Support
            return ItemStack.EMPTY;
        } else {
            final Identifier identifier = new Identifier("minecraft:" + type.toLowerCase());
            if (!Registries.ITEM.containsId(identifier)) {
                Utils.warn("Invalid JeiIngredient Item Id: " + id);
                return ItemStack.EMPTY;
            }
            final ItemStack itemStack = Registries.ITEM.get(identifier).getDefaultStack().copy();
            itemStack.setCount(amount);
            return itemStack;
        }
    }
}

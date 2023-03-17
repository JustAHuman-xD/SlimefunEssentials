package me.justahuman.slimefun_essentials.compat.jei.categories;

import me.justahuman.slimefun_essentials.api.IdInterpreter;
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
import mezz.jei.fabric.ingredients.fluid.JeiFluidIngredient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ProcessCategory implements IRecipeCategory<SlimefunRecipe>, IdInterpreter<Object> {
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
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        int x = 0;
        for (SlimefunRecipeComponent input : recipe.inputs()) {
            addIngredients(builder.addSlot(RecipeIngredientRole.INPUT, x, 0), input);
            x++;
        }
        
        x = 0;
        for (SlimefunRecipeComponent output : recipe.outputs()) {
            addIngredients(builder.addSlot(RecipeIngredientRole.OUTPUT, x, 0), output);
            x++;
        }
    }
    
    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
    
    }
    
    public void addIngredients(IRecipeSlotBuilder slotBuilder, SlimefunRecipeComponent component) {
        for (String id : component.getMultiId() != null ? component.getMultiId() : List.of(component.getId())) {
            addIngredientObject(slotBuilder, interpretId(id, ItemStack.EMPTY));
        }
    }
    
    public void addIngredientObject(IRecipeSlotBuilder slotBuilder, Object ingredient) {
        if (ingredient instanceof List<?> list) {
            for (Object object : list) {
                addIngredientObject(slotBuilder, object);
            }
        } else if (ingredient instanceof ItemStack itemStack) {
            slotBuilder.addItemStack(itemStack);
        } else if (ingredient instanceof SlimefunItemStack slimefunItemStack) {
            slotBuilder.addIngredient(JeiIntegration.SLIMEFUN, slimefunItemStack);
        } else if (ingredient instanceof JeiFluidIngredient fluidStack) {
            slotBuilder.addFluidStack(fluidStack.getFluid(), fluidStack.getAmount());
        }
    }
    
    @Override
    public Object fromTag(TagKey<Item> tagKey, int amount, Object defaultValue) {
        Optional<RegistryEntryList.Named<Item>> optional = Registries.ITEM.getEntryList(tagKey);
        if (optional.isEmpty()) {
            return defaultValue;
        }
        
        return optional.get().stream().map(ItemStack::new).toList();
    }
    
    @Override
    public Object fromItemStack(ItemStack itemStack, int amount, Object defaultValue) {
        itemStack.setCount(amount);
        return itemStack;
    }
    
    @Override
    public Object fromSlimefunItemStack(SlimefunItemStack slimefunItemStack, int amount, Object defaultValue) {
        return slimefunItemStack.setAmount(amount);
    }
    
    @Override
    public Object fromFluid(Fluid fluid, int amount, Object defaultValue) {
        return new JeiFluidIngredient(fluid, amount);
    }
    
    @Override
    public Object fromEntityType(EntityType<?> entityType, int amount, Object defaultValue) {
        // TODO: add support for entities
        return defaultValue;
    }
}

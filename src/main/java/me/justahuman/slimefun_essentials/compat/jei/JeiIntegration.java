package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers.SlimefunStackHelper;
import me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers.SlimefunStackRenderer;
import me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers.SlimefunStackType;
import me.justahuman.slimefun_essentials.compat.jei.categories.ProcessCategory;
import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@JeiPlugin
public class JeiIntegration implements IModPlugin {
    public static final SlimefunStackType SLIMEFUN = new SlimefunStackType();
    public static final JeiRecipeInterpreter RECIPE_INTERPRETER = new JeiRecipeInterpreter();
    
    @Override
    @NotNull
    public Identifier getPluginUid() {
        return Utils.newIdentifier("jei_integration");
    }
    
    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(SLIMEFUN, ResourceLoader.getSlimefunItems().values(), new SlimefunStackHelper(), new SlimefunStackRenderer());
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            registration.addRecipeCategories(new ProcessCategory(guiHelper, slimefunCategory, ResourceLoader.getSlimefunItems().get(slimefunCategory.id()).itemStack()));
        }
    }
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        final IIngredientManager ingredientManager = registration.getIngredientManager();
        final Collection<ItemStack> slimefunStacks = ResourceLoader.getSlimefunItems().values().stream().map(SlimefunItemStack::itemStack).collect(Collectors.toSet());
        final Collection<ItemStack> duplicateIngredients = new HashSet<>();
        for (ItemStack ingredient : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            if (slimefunStacks.contains(ingredient)) {
                duplicateIngredients.add(ingredient);
            }
        }
        ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, duplicateIngredients);
        
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            registration.addRecipes(RecipeType.create(Utils.ID, slimefunCategory.id().toLowerCase(), SlimefunRecipe.class), slimefunCategory.recipes());
        }
    }
}

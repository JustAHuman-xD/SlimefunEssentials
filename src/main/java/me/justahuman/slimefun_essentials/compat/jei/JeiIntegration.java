package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.screen.ReloadingScreen;
import me.justahuman.slimefun_essentials.mixins.jei.InterpretersAccessor;
import me.justahuman.slimefun_essentials.client.payload.Payloads;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import mezz.jei.library.load.registration.SubtypeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@JeiPlugin
public class JeiIntegration implements IModPlugin {
    private static final Set<SlimefunJeiCategory> CATEGORIES = new HashSet<>();
    private static boolean mustQueue = false;
    private static boolean runtimeAvailable = false;

    public static final JeiRecipeInterpreter RECIPE_INTERPRETER = new JeiRecipeInterpreter();

    @Override
    public @NotNull Identifier getPluginUid() {
        return SlimefunEssentials.id("jei_integration");
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        for (SlimefunJeiCategory category : CATEGORIES) {
            category.updateIcon();
        }

        registration.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
                SlimefunRegistry.getSlimefunItems().values().stream().map(SlimefunItemStack::itemStack).toList());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration iregistration) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        if (!(iregistration instanceof SubtypeRegistration registration)) {
            SlimefunEssentials.LOGGER.error("Failed to register JEI item subtypes: SubtypeRegistration is not an instance of SubtypeRegistration");
            return;
        }

        SubtypeInterpreters interpreters = registration.getInterpreters();
        Set<Item> wrappedItems = new HashSet<>();
        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSlimefunItems().values()) {
            ItemStack itemStack = slimefunItemStack.itemStack();
            Item item = itemStack.getItem();
            if (!wrappedItems.add(item)) {
                continue;
            }

            ISubtypeInterpreter<ItemStack> oldInterpreter = interpreters.get(VanillaTypes.ITEM_STACK, itemStack);
            ISubtypeInterpreter<ItemStack> newInterpreter = new SlimefunIdInterpreter(oldInterpreter);
            ((InterpretersAccessor) interpreters).getMap().put(item, newInterpreter);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            SlimefunJeiCategory category = new SlimefunJeiCategory(guiHelper, recipeCategory);
            registration.addRecipeCategories(category);
            CATEGORIES.add(category);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            registration.addRecipes(RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class), recipeCategory.childRecipes());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            registration.addRecipeCatalyst(recipeCategory.itemStack(), RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class));
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtimeAvailable = true;
        if (mustQueue) {
            if (Payloads.metExpected()) {
                reload();
            } else {
                Payloads.onMeetExpected(JeiIntegration::reload);
            }
            mustQueue = false;
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        runtimeAvailable = false;
    }

    private static void reload() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            AtomicBoolean started = new AtomicBoolean(false);
            client.setScreen(new ReloadingScreen(
                    () -> !started.get() || !runtimeAvailable,
                    () -> client.setScreen(null)
            ));
            JeiReloader.reload();
            started.set(true);
            mustQueue = false;
        });
    }
}
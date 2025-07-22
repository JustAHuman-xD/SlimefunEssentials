package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.screen.ReloadingScreen;
import me.justahuman.slimefun_essentials.utils.Payloads;
import me.shedaniel.rei.RoughlyEnoughItemsCoreClient;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReiIntegration implements REIClientPlugin {
    public static final ReiRecipeInterpreter RECIPE_INTERPRETER = new ReiRecipeInterpreter();
    private static boolean mustQueue = false;

    @Override
    public double getPriority() {
        return 10;
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.registerGlobal(new SlimefunIdComparator());
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSlimefunItems().values()) {
            registry.addEntry(EntryStacks.of(slimefunItemStack.itemStack()));
        }
    }
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            final ItemStack icon = recipeCategory.itemStack();
            final DisplayCategory<?> displayCategory = new SlimefunReiCategory(recipeCategory, icon);
            registry.add(displayCategory);
            registry.addWorkstations(displayCategory.getCategoryIdentifier(), EntryStacks.of(icon));
        }
    }
    
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (mustQueue || !Payloads.metExpected()) {
            mustQueue = true;
            return;
        }

        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            for (SlimefunRecipe slimefunRecipe : recipeCategory.childRecipes()) {
                registry.add(new SlimefunDisplay(recipeCategory, slimefunRecipe));
            }
        }
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (mustQueue && stage == ReloadStage.END) {
            if (Payloads.metExpected()) {
                reload();
            } else {
                Payloads.onMeetExpected(ReiIntegration::reload);
            }
        }
    }

    private static void reload() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            AtomicBoolean started = new AtomicBoolean(false);
            client.setScreen(new ReloadingScreen(
                    () -> !started.get() || PluginManager.areAnyReloading(),
                    () -> client.setScreen(null)
            ));
            RoughlyEnoughItemsCoreClient.reloadPlugins(null, null);
            started.set(true);
            mustQueue = false;
        });
    }
}

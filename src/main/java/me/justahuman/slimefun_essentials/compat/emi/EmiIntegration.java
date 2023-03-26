package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.AncientAltarRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.GridRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.ProcessRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.ReactorRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.SmelteryRecipe;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EmiIntegration implements EmiPlugin {
    public static final EmiRecipeInterpreter RECIPE_INTERPRETER = new EmiRecipeInterpreter();
    private static final Map<String, SlimefunEmiCategory> slimefunCategories = new HashMap<>();
    
    @Override
    public void register(EmiRegistry emiRegistry) {
        for (Map.Entry<String, SlimefunItemStack> entry : ResourceLoader.getSlimefunItems().entrySet()) {
            emiRegistry.setDefaultComparison(EmiStack.of(entry.getValue().itemStack()), original -> original.copy().nbt(true).build());
        }
        
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            final String workstationId = slimefunCategory.id();
            final String type = slimefunCategory.type();
            final Identifier categoryIdentifier = Utils.newIdentifier(workstationId);
            final EmiStack workStation = RECIPE_INTERPRETER.emiStackFromId(workstationId + ":1");
            final SlimefunEmiCategory slimefunEmiCategory;
            if (slimefunCategories.containsKey(workstationId)) {
                slimefunEmiCategory = slimefunCategories.get(workstationId);
            } else {
                slimefunEmiCategory = new SlimefunEmiCategory(emiRegistry, categoryIdentifier, workStation);
                slimefunCategories.put(workstationId, slimefunEmiCategory);
                emiRegistry.addCategory(slimefunEmiCategory);
            }
            
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                emiRegistry.addRecipe(getEmiRecipe(slimefunCategory, slimefunRecipe, slimefunEmiCategory, type));
            }
        }
    }

    public static EmiRecipe getEmiRecipe(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, EmiRecipeCategory category, String type) {
        if (type.equals("ancient_altar")) {
            return new AncientAltarRecipe(slimefunCategory, slimefunRecipe, category);
        } else if (type.equals("smeltery")) {
            return new SmelteryRecipe(slimefunCategory, slimefunRecipe, category);
        } else if (type.equals("reactor")) {
            return new ReactorRecipe(slimefunCategory, slimefunRecipe, category);
        } else if (type.contains("grid")) {
            return new GridRecipe(slimefunCategory, slimefunRecipe, category, type);
        } else {
            return new ProcessRecipe(slimefunCategory, slimefunRecipe, category);
        }
    }
}

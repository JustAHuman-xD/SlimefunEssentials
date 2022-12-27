package me.justahuman.slimefun_essentials.compatibility.emi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.bom.BoM;
import me.justahuman.slimefun_essentials.compatibility.emi.misc.Category;
import me.justahuman.slimefun_essentials.compatibility.emi.misc.EmiCondition;
import me.justahuman.slimefun_essentials.compatibility.emi.misc.EntityEmiStack;
import me.justahuman.slimefun_essentials.compatibility.emi.recipehandler.MultiblockHandler;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.AncientAltarRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.ProcessRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.ReactorRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.SmelteryRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.ThreeByThreeRecipe;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.CategoryContainer;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.ConditionContainer;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.CustomMultiStack;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.RecipeContainer;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.RecipeLoader;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.TagStack;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.TypeStack;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.core.Utils;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmiIntegration implements EmiPlugin {
    //Data Loading Things
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();

    //Defaults
    private static final InputStream defaultStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("defaults.json");
    private static final JsonObject defaultObject = defaultStream != null ? GSON.fromJson(new InputStreamReader(defaultStream), JsonObject.class) : null;

    @Override
    public void register(EmiRegistry emiRegistry) {
        EmiUtils.load();
        RecipeLoader.load();
        emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_3X3, new MultiblockHandler());

        final Map<String, TypeStack> items = RecipeLoader.getItems();
        final Map<String, CategoryContainer> categories = RecipeLoader.getCategories();
        final JsonArray defaults = defaultObject.getAsJsonArray(getDefaults());
        final JsonArray otherDefaults = defaultObject.getAsJsonArray(getOtherDefaults());

        for (String categoryId : categories.keySet()) {
            final CategoryContainer categoryContainer = categories.get(categoryId);
            final EmiRecipeCategory emiRecipeCategory = new Category(categoryContainer.getId(), EmiStack.of(categoryContainer.getWorkstation()).comparison(original -> original.copy().nbt(true).build()).copy(), categoryContainer.getWorkstation().getName());
            emiRegistry.addCategory(emiRecipeCategory);
            emiRegistry.addWorkstation(emiRecipeCategory, EmiStack.of(categoryContainer.getWorkstation()).comparison(original -> original.copy().nbt(true).build()).copy());

            for (RecipeContainer recipeContainer : categoryContainer.getRecipes()) {
                final List<CustomMultiStack> customInputs = recipeContainer.inputs();
                final List<CustomMultiStack> customOutputs = recipeContainer.outputs();
                final List<ConditionContainer> customConditions = recipeContainer.conditions();
                final Integer energy = recipeContainer.energy();
                final Integer ticks = recipeContainer.ticks();
                final Identifier id = recipeContainer.id();
                final String recipeType = recipeContainer.type();

                final List<EmiIngredient> inputs = new ArrayList<>();
                final List<EmiStack> outputs = new ArrayList<>();
                final List<EmiCondition> conditions = new ArrayList<>();

                for (CustomMultiStack customMultiStack : customInputs) {
                    inputs.add(toIngredient(customMultiStack));
                }

                for (CustomMultiStack customMultiStack : customOutputs) {
                    outputs.add(toStack(customMultiStack));
                }
                
                for (ConditionContainer conditionContainer : customConditions) {
                    conditions.add(toCondition(conditionContainer));
                }

                //Get and Register the Recipe
                final EmiRecipe emiRecipe = getRecipe(recipeType, emiRecipeCategory, id, inputs, outputs, ticks, energy, conditions);
                emiRegistry.addRecipe(emiRecipe);

                //Add default Recipes
                if (defaults.contains(new JsonPrimitive(id.toString()))) {
                    for (EmiStack output : outputs) {
                        EmiRecipe defaultRecipe = BoM.getRecipe(output);
                        if (defaultRecipe != null && defaultRecipe.getId() != null && otherDefaults.contains(new JsonPrimitive(defaultRecipe.getId().toString()))) {
                            BoM.removeRecipe(defaultRecipe);
                            BoM.addRecipe(emiRecipe, output);
                        } else if (defaultRecipe == null) {
                            BoM.addRecipe(emiRecipe, output);
                        }
                    }
                }
            }
        }

        for (String itemId : items.keySet()) {
            emiRegistry.addEmiStack(EmiStack.of(items.get(itemId).itemStack()).comparison(original -> original.copy().nbt(true).build()).copy());
        }
        RecipeLoader.clear();
    }

    private EmiIngredient toIngredient(CustomMultiStack customMultiStack) {
        final EmiIngredient toReturn;
        final List<Object> stacks = customMultiStack.stacks();
        final long amount = customMultiStack.amount();

        if (stacks.size() == 1) {
            final Object stack = stacks.get(0);
            toReturn = convertObject(stack, amount);
        } else {
            final List<EmiIngredient> multiples = new ArrayList<>();
            for (Object stack : stacks) {
                multiples.add(convertObject(stack, 1));
            }
            toReturn = EmiIngredient.of(multiples, amount);
        }

        return toReturn;
    }

    private EmiStack toStack(CustomMultiStack customMultiStack) {
        final List<Object> stacks = customMultiStack.stacks();
        final Object stack = stacks.get(0);
        final long amount = customMultiStack.amount();

        return (EmiStack) convertObject(stack, amount);
    }

    private EmiIngredient convertObject(Object stack, long amount) {
        if (stack instanceof ItemStack itemStack) {
            return EmiStack.of(itemStack).comparison(original -> original.copy().nbt(true).build()).copy().setAmount(amount);
        } else if (stack instanceof Entity entity) {
            return new EntityEmiStack(entity).comparison(original -> original.copy().nbt(true).build()).copy().setAmount(amount);
        } else if (stack instanceof Fluid fluid) {
            return EmiStack.of(fluid).comparison(original -> original.copy().nbt(true).build()).copy().setAmount(amount);
        } else if (stack instanceof TagStack tagStack) {
            return EmiIngredient.of(tagStack.tagKey(), tagStack.amount());
        } else {
            return EmiStack.EMPTY;
        }
    }
    
    private EmiCondition toCondition(ConditionContainer conditionContainer) {
        final Identifier identifier = conditionContainer.identifier();
        final String id = conditionContainer.id();
        final int x = conditionContainer.x();
        final int y = conditionContainer.y();
        final int width = conditionContainer.width();
        final int height = conditionContainer.height();
        return new EmiCondition(id, new EmiTexture(identifier, x, y, width, height));
    }

    private EmiRecipe getRecipe(String type, EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer ticks, Integer energy, List<EmiCondition> conditions) {
        return switch(type) {
            case "ancient_altar" -> new AncientAltarRecipe(emiRecipeCategory, id, inputs, outputs);
            case "process" -> new ProcessRecipe(emiRecipeCategory, id, inputs, outputs, ticks, energy, conditions);
            case "reactor" -> new ReactorRecipe(emiRecipeCategory, id, inputs, outputs, ticks, energy);
            case "smeltery" ->  new SmelteryRecipe(emiRecipeCategory, id, inputs, outputs, ticks, energy);
            default -> new ThreeByThreeRecipe(emiRecipeCategory, id, inputs, outputs, ticks, energy);
        };
    }

    private String getDefaults() {
        String toReturn = "multiblocks";
        if (Utils.isClothConfigEnabled()) {
            final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            toReturn = modConfig.useMachineDefaults() ? "machines" : "multiblocks";
        }
        return toReturn;
    }

    private String getOtherDefaults() {
        String toReturn = "machines";
        if (Utils.isClothConfigEnabled()) {
            final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            toReturn = ! modConfig.useMachineDefaults() ? "machines" : "multiblocks";
        }
        return toReturn;
    }
}

package me.justahuman.slimefun_essentials.compatibility.emi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.bom.BoM;
import me.justahuman.slimefun_essentials.Utils;
import me.justahuman.slimefun_essentials.compatibility.emi.misc.Category;
import me.justahuman.slimefun_essentials.compatibility.emi.misc.EntityEmiStack;
import me.justahuman.slimefun_essentials.compatibility.emi.recipehandler.MultiblockHandler;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.AncientAltarRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.KillRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.MachineRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.MultiblockRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.OtherRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.SmelteryRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.ThreeByThreeRecipe;
import me.justahuman.slimefun_essentials.compatibility.emi.recipetype.TradeRecipe;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.CategoryContainer;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.CustomMultiStack;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.RecipeContainer;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.RecipeLoader;
import me.justahuman.slimefun_essentials.compatibility.recipe_common.TagStack;
import me.justahuman.slimefun_essentials.config.ModConfig;
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
        RecipeLoader.load();
        emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_3X3, new MultiblockHandler());

        final Map<String, ItemStack> items = RecipeLoader.getItems();
        final Map<String, CategoryContainer> categories = RecipeLoader.getCategories();
        final JsonArray defaults = defaultObject.getAsJsonArray(getDefaults());
        final JsonArray otherDefaults = defaultObject.getAsJsonArray(getOtherDefaults());

        for (String categoryId : categories.keySet()) {
            final CategoryContainer categoryContainer = categories.get(categoryId);
            final EmiRecipeCategory emiRecipeCategory = new Category(categoryContainer.getId(), EmiStack.of(categoryContainer.getWorkstation()).comparison(original -> original.copy().nbt(true).build()).copy(), categoryContainer.getWorkstation().getName());
            emiRegistry.addCategory(emiRecipeCategory);
            emiRegistry.addWorkstation(emiRecipeCategory, EmiStack.of(categoryContainer.getWorkstation()).comparison(original -> original.copy().nbt(true).build()).copy());

            for (RecipeContainer recipeContainer : categoryContainer.getRecipes()) {
                final List<CustomMultiStack> customInputs = recipeContainer.getInputs();
                final List<CustomMultiStack> customOutputs = recipeContainer.getOutputs();
                final Integer energy = recipeContainer.getEnergy();
                final Integer ticks = recipeContainer.getTicks();
                final Identifier id = recipeContainer.getId();
                final String recipeType = recipeContainer.getType();

                final List<EmiIngredient> inputs = new ArrayList<>();
                final List<EmiStack> outputs = new ArrayList<>();

                for (CustomMultiStack customMultiStack : customInputs) {
                    inputs.add(toIngredient(customMultiStack));
                }

                for (CustomMultiStack customMultiStack : customOutputs) {
                    outputs.add(toStack(customMultiStack));
                }

                //Get and Register the Recipe
                final EmiRecipe emiRecipe = getRecipe(recipeType, emiRecipeCategory, id, inputs, outputs, ticks, energy);
                emiRegistry.addRecipe(emiRecipe);

                //Add default Recipes
                if (defaults.contains(new JsonPrimitive(id.toString()))) {
                    for (EmiStack output : outputs) {
                        EmiRecipe defaultRecipe = BoM.getRecipe(output);
                        if (defaultRecipe != null && otherDefaults.contains(new JsonPrimitive(defaultRecipe.getId().toString()))) {
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
            emiRegistry.addEmiStack(EmiStack.of(items.get(itemId)).comparison(original -> original.copy().nbt(true).build()).copy());
        }
        RecipeLoader.clear();
    }

    private EmiIngredient toIngredient(CustomMultiStack customMultiStack) {
        final EmiIngredient toReturn;
        final List<Object> stacks = customMultiStack.getStacks();
        final long amount = customMultiStack.getAmount();

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
        final List<Object> stacks = customMultiStack.getStacks();
        final Object stack = stacks.get(0);
        final long amount = customMultiStack.getAmount();

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
            return EmiIngredient.of(tagStack.tagKey(), tagStack.getAmount());
        } else {
            return EmiStack.EMPTY;
        }
    }

    private EmiRecipe getRecipe(String type, EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer ticks, Integer energy) {
        final EmiRecipe recipe;
        switch (type) {
            case "ancient_altar":
                recipe = new AncientAltarRecipe(emiRecipeCategory, id, inputs, outputs);
                break;
            case "machine":
                if (ticks == 0 || energy == 0) {
                    recipe = new OtherRecipe(emiRecipeCategory, id, inputs, outputs);
                } else {
                    recipe = new MachineRecipe(emiRecipeCategory, id, inputs, outputs, ticks, energy);
                }
                break;
            case "trade":
                recipe = new TradeRecipe(emiRecipeCategory, id, inputs, outputs);
                break;
            case "kill":
                recipe = new KillRecipe(emiRecipeCategory, id, inputs, outputs);
                break;
            case "smeltery":
                recipe = new SmelteryRecipe(emiRecipeCategory, id, inputs, outputs, ticks, energy);
                break;
            case "3by3":
                recipe = new ThreeByThreeRecipe(emiRecipeCategory, id, inputs, outputs);
                break;
            default:
                recipe = new MultiblockRecipe(emiRecipeCategory, id, inputs, outputs);
                break;
        }
        return recipe;
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

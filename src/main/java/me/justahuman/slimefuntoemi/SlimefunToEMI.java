package me.justahuman.slimefuntoemi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.EmiStackSerializer;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmptyEmiStack;
import me.justahuman.slimefuntoemi.recipehandler.MultiblockHandler;
import me.justahuman.slimefuntoemi.recipetype.AncientAltarRecipe;
import me.justahuman.slimefuntoemi.recipetype.KillRecipe;
import me.justahuman.slimefuntoemi.recipetype.MachineRecipe;
import me.justahuman.slimefuntoemi.recipetype.MultiBlockRecipe;
import me.justahuman.slimefuntoemi.recipetype.OtherRecipe;
import me.justahuman.slimefuntoemi.recipetype.SmelteryRecipe;
import me.justahuman.slimefuntoemi.recipetype.TradeRecipe;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlimefunToEMI implements EmiPlugin, ClientModInitializer {
    public static final int bigSlotHeight = 26;
    public static final int bigSlotWidth = 26;
    public static final int slotHeight = 18;
    public static final int slotWidth = 18;
    public static final int arrowHeight = 17;
    public static final int arrowWidth = 24;
    public static final int chargeWidth = 7;
    public static final int chargeHeight = 9;
    public static final Identifier WIDGETS = new Identifier("sftoemi", "textures/gui/widgets.png");
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(WIDGETS, 36, 0, chargeWidth, chargeHeight);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(WIDGETS, 43, 0, chargeWidth, chargeHeight);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(WIDGETS, 50, 0, chargeWidth, chargeHeight);
    protected static final Map<String, EmiStack> recipeTypes = new HashMap<>();
    protected static final Map<String, EmiStack> multiblocks = new HashMap<>();
    protected static final Map<String, EmiStack> items = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger("sftoemi");
    private static final String errorMessage = "[SFtoEMI] Failed to parse persistent data";
    private static final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.json");
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final JsonObject jsonObject = inputStream != null ? GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class) : null;

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_3X3, new MultiblockHandler());
        final JsonObject totalRecipes = jsonObject != null ? jsonObject.getAsJsonObject("recipes") : jsonObject;
        if (totalRecipes != null) {
            for (String workstationId : totalRecipes.keySet()) {

                final EmiStack workstationStack = getWorkstationStack(workstationId);
                final JsonArray recipes = totalRecipes.getAsJsonArray(workstationId);

                if (recipes == null || workstationStack == null) {
                    continue;
                }

                final String categoryId = "sftoemi:" + getCategoryId(workstationId).toLowerCase();
                final EmiRecipeCategory emiRecipeCategory;

                if (getCategoryId(workstationId).equals("machine") || getCategoryId(workstationId).equals("smeltery")) {
                    emiRecipeCategory = new Category(new Identifier(categoryId), workstationStack, workstationStack.getItemStack().getName());
                } else {
                    emiRecipeCategory = new EmiRecipeCategory(new Identifier(categoryId), workstationStack);
                }

                emiRegistry.addCategory(emiRecipeCategory);
                if (! workstationStack.isEmpty()) {
                    emiRegistry.addWorkstation(emiRecipeCategory, workstationStack);
                }


                for (JsonElement recipeElement : recipes) {

                    if (! (recipeElement instanceof JsonObject recipe)) {
                        continue;
                    }

                    final JsonArray recipeInputs = recipe.getAsJsonArray("inputs");
                    final JsonArray recipeOutputs = recipe.getAsJsonArray("outputs");
                    final Integer ticks = recipe.get("time") != null ? recipe.get("time").getAsInt() : 0;
                    final Integer energy = recipe.get("energy") != null ? recipe.get("energy").getAsInt() : 0;

                    if (recipeInputs == null || recipeOutputs == null) {
                        continue;
                    }

                    final List<EmiIngredient> inputs = new ArrayList<>();
                    final List<EmiStack> outputs = new ArrayList<>();

                    for (JsonElement jsonElement : recipeInputs) {
                        if (jsonElement instanceof JsonArray jsonArray) {
                            final List<EmiStack> multiples = new ArrayList<>();
                            for (JsonElement subJsonElement : jsonArray) {
                                final String input = subJsonElement.getAsString();
                                if (input.equals("")) {
                                    inputs.add(EmiIngredient.of(Ingredient.EMPTY));
                                } else {
                                    final String inputId = input.substring(0, input.indexOf(":"));
                                    final int amount = Integer.parseInt(input.substring(input.indexOf(":") + 1));
                                    multiples.add(getStackFromId(inputId).copy().setAmount(amount));
                                }
                            }
                            inputs.add(EmiIngredient.of(multiples));
                        } else {
                            final String input = jsonElement.getAsString();
                            if (input.equals("")) {
                                inputs.add(EmiIngredient.of(Ingredient.EMPTY));
                            } else {
                                final String inputId = input.substring(0, input.indexOf(":"));
                                final int amount = Integer.parseInt(input.substring(input.indexOf(":") + 1));
                                inputs.add(getStackFromId(inputId).copy().setAmount(amount));
                            }
                        }
                    }

                    for (JsonElement jsonElement : recipeOutputs) {
                        final String output = jsonElement.getAsString();
                        if (! output.equals("")) {
                            final String outputId = output.substring(0, output.indexOf(":"));
                            final int amount = Integer.parseInt(output.substring(output.indexOf(":") + 1));
                            outputs.add(getStackFromId(outputId).copy().setAmount(amount));
                        }
                    }

                    addRecipe(emiRegistry, getCategoryId(workstationId), emiRecipeCategory, inputs, outputs, ticks, energy);
                }
            }
        }

        final List<String> itemIds = new ArrayList<>(items.keySet());
        itemIds.sort(Comparator.naturalOrder());
        for (String itemId : itemIds) {
            emiRegistry.addEmiStack(items.get(itemId));
        }
    }

    @Override
    public void onInitializeClient() {
        loadItems();
    }

    private void addRecipe(EmiRegistry emiRegistry, String type, EmiRecipeCategory emiRecipeCategory, List<EmiIngredient> inputs, List<EmiStack> outputs, Integer ticks, Integer energy) {
        final EmiRecipe recipe;
        switch (type) {
            case "ancient_altar":
                recipe = new AncientAltarRecipe(emiRecipeCategory, null, inputs, outputs);
                break;
            case "machine":
                if (ticks == 0 || energy == 0) {
                    recipe = new OtherRecipe(emiRecipeCategory, null, inputs, outputs);
                } else {
                    recipe = new MachineRecipe(emiRecipeCategory, null, inputs, outputs, ticks, energy);
                }
                break;
            case "trade":
                recipe = new TradeRecipe(emiRecipeCategory, null, inputs, outputs);
                break;
            case "kill":
                recipe = new KillRecipe(emiRecipeCategory, null, inputs, outputs);
                break;
            case "smeltery":
                recipe = new SmelteryRecipe(emiRecipeCategory, null, inputs, outputs, ticks, energy);
                break;
            default:
                recipe = new MultiBlockRecipe(emiRecipeCategory, null, inputs, outputs);
                break;
        }
        emiRegistry.addRecipe(recipe);
    }

    private String getCategoryId(String workstationId) {
        if (multiblocks.containsKey(workstationId)) {
            return "multiblock." + workstationId;
        } else if (workstationId.contains("ANCIENT_ALTAR")) {
            return "ancient_altar";
        } else if (workstationId.contains("TRADE_PIGLIN")) {
            return "trade";
        } else if (workstationId.contains("KILL")) {
            return "kill";
        } else if (workstationId.contains("USE")) {
            return "use";
        } else if (workstationId.contains("SMELTERY")) {
            return "smeltery";
        } else if (items.containsKey(workstationId) || recipeTypes.containsKey(workstationId)) {
            return "machine";
        }
        return "ERROR";
    }

    private EmiStack getWorkstationStack(String workstationId) {
        if (multiblocks.containsKey(workstationId)) {
            return multiblocks.get(workstationId);
        } else if (items.containsKey(workstationId)) {
            return items.get(workstationId);
        } else if (recipeTypes.containsKey(workstationId)) {
            return recipeTypes.get(workstationId);
        }
        return new EmptyEmiStack();
    }

    private EmiStack getStackFromId(String id) {
        if (items.containsKey(id)) {
            return items.get(id).comparison(original -> original.copy().nbt(true).build()).setAmount(1);
        } else {
            if (id.equals("iron_golem")) {
                return EmiStack.EMPTY;
            } else {
                return EmiStack.of(Registry.ITEM.get(new Identifier("minecraft:" + id.toLowerCase())));
            }
        }
    }

    private void loadItems() {
        if (jsonObject == null) {
            return;
        }
        try {
            final JsonObject itemsJson = jsonObject.getAsJsonObject("items");
            final JsonObject recipeTypeJson = jsonObject.getAsJsonObject("recipe_types");
            final JsonObject multiblockJson = jsonObject.getAsJsonObject("multiblocks");

            if (itemsJson != null && ! itemsJson.keySet().isEmpty()) {
                for (String slimefunId : itemsJson.keySet()) {
                    final JsonObject object = itemsJson.getAsJsonObject(slimefunId);
                    if (object != null) {
                        items.put(slimefunId, (EmiStack) EmiStackSerializer.deserialize(object));
                    }
                }
            }
            if (multiblockJson != null && ! multiblockJson.keySet().isEmpty()) {
                for (String multiblockId : multiblockJson.keySet()) {
                    final JsonObject object = multiblockJson.getAsJsonObject(multiblockId);
                    if (object != null) {
                        multiblocks.put(multiblockId, (EmiStack) EmiStackSerializer.deserialize(object));
                    }
                }
            }
            if (recipeTypeJson != null && ! recipeTypeJson.keySet().isEmpty()) {
                for (String recipeTypeId : recipeTypeJson.keySet()) {
                    final JsonObject object = recipeTypeJson.getAsJsonObject(recipeTypeId);
                    if (object != null) {
                        recipeTypes.put(recipeTypeId, (EmiStack) EmiStackSerializer.deserialize(object));
                    }
                }
            }

        } catch (Exception e) {
            logger.error(errorMessage);
            e.printStackTrace();
        }
    }
}

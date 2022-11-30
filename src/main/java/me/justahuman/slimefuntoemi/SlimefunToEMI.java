package me.justahuman.slimefuntoemi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.emi.emi.EmiStackSerializer;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmptyEmiStack;
import dev.emi.emi.bom.BoM;
import me.justahuman.slimefuntoemi.config.ModConfig;
import me.justahuman.slimefuntoemi.recipehandler.MultiblockHandler;
import me.justahuman.slimefuntoemi.recipetype.AncientAltarRecipe;
import me.justahuman.slimefuntoemi.recipetype.KillRecipe;
import me.justahuman.slimefuntoemi.recipetype.MachineRecipe;
import me.justahuman.slimefuntoemi.recipetype.MultiBlockRecipe;
import me.justahuman.slimefuntoemi.recipetype.OtherRecipe;
import me.justahuman.slimefuntoemi.recipetype.SmelteryRecipe;
import me.justahuman.slimefuntoemi.recipetype.TradeRecipe;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlimefunToEMI implements EmiPlugin, ClientModInitializer {
    //Data Processing Maps
    protected static final Map<String, EmiStack> recipeTypes = new HashMap<>();
    protected static final Map<String, EmiStack> multiblocks = new HashMap<>();
    protected static final Map<String, EmiStack> items = new HashMap<>();
    //Data Loading Things
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.json");
    private static final JsonObject jsonObject = dataStream != null ? GSON.fromJson(new InputStreamReader(dataStream), JsonObject.class) : null;
     //Defaults
    private static final InputStream defaultStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("defaults.json");
    private static final JsonObject defaultObject = defaultStream != null ? GSON.fromJson(new InputStreamReader(defaultStream), JsonObject.class) : null;

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_3X3, new MultiblockHandler());
        loadItems();

        final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        final JsonArray defaults = defaultObject.getAsJsonArray(modConfig.getUseMachineDefaults() ? "machines" : "multiblocks");
        final JsonArray otherDefaults = defaultObject.getAsJsonArray(! modConfig.getUseMachineDefaults() ? "machines" : "multiblocks");
        final JsonObject totalRecipes = jsonObject != null ? jsonObject.getAsJsonObject("recipes") : null;

        if (totalRecipes != null && defaults != null) {
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

                    //Define important Variables
                    final List<EmiIngredient> inputs = new ArrayList<>();
                    final List<EmiStack> outputs = new ArrayList<>();
                    final StringBuilder uniqueId = new StringBuilder().append("sftoemi:/").append(workstationId.toLowerCase());
                    final JsonArray recipeInputs = recipe.getAsJsonArray("inputs");
                    final JsonArray recipeOutputs = recipe.getAsJsonArray("outputs");
                    final Integer ticks = recipe.get("time") != null ? recipe.get("time").getAsInt() : 0;
                    final Integer energy = recipe.get("energy") != null ? recipe.get("energy").getAsInt() : 0;

                    if (recipeInputs == null || recipeOutputs == null) {
                        continue;
                    }

                    //Fill Inputs
                    for (JsonElement jsonElement : recipeInputs) {
                        if (jsonElement instanceof JsonArray jsonArray) {
                            final List<EmiStack> multiples = new ArrayList<>();
                            for (JsonElement subJsonElement : jsonArray) {
                                final EmiStack input = getStackFromElement(subJsonElement, false);
                                if (input != null) {
                                    multiples.add(input);
                                }
                            }
                            inputs.add(EmiIngredient.of(multiples));
                        } else {
                            final EmiStack input = getStackFromElement(jsonElement, true);
                            if (input != null) {
                                inputs.add(input);
                            }
                        }
                    }

                    //Fill Outputs
                    for (JsonElement jsonElement : recipeOutputs) {
                        final EmiStack output = getStackFromElement(jsonElement, false);
                        if (output != null) {
                            outputs.add(output);
                        }
                    }

                    //Remove any parts of the String that cannot be an Identifier
                    for (char x : recipe.toString().toLowerCase().toCharArray()) {
                        if (Character.isLetterOrDigit(x)) {
                            uniqueId.append(x);
                        }
                    }

                    if (emiRegistry.getRecipeManager().get(new Identifier(uniqueId.toString())).isPresent()) {
                        continue;
                    }

                    //Get and Register the Recipe
                    final EmiRecipe emiRecipe = getRecipe(getCategoryId(workstationId), emiRecipeCategory, new Identifier(uniqueId.toString()), inputs, outputs, ticks, energy);
                    emiRegistry.addRecipe(emiRecipe);

                    //Add default Recipes
                    if (defaults.contains(new JsonPrimitive(uniqueId.toString()))) {
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
        }

        final List<String> itemIds = new ArrayList<>(items.keySet());
        itemIds.sort(Comparator.naturalOrder());
        for (String itemId : itemIds) {
            emiRegistry.addEmiStack(items.get(itemId));
        }

        recipeTypes.clear();
        multiblocks.clear();
        items.clear();
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
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
            default:
                recipe = new MultiBlockRecipe(emiRecipeCategory, id, inputs, outputs);
                break;
        }
        return recipe;
    }

    private String getCategoryId(String workstationId) {
        if (multiblocks.containsKey(workstationId)) {
            return "multiblock." + workstationId;
        } else if (workstationId.contains("ANCIENT_ALTAR")) {
            return "ancient_altar";
        } else if (workstationId.contains("TRADE")) {
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
        return "error";
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
        final String first = id.substring(0, id.indexOf(":"));
        final String second = id.substring(id.indexOf(":") + 1);
        if (items.containsKey(first)) {
            return items.get(first).comparison(original -> original.copy().nbt(true).build()).copy().setAmount(Integer.parseInt(second));
        } else {
            if (first.equals("entity")) {
                return new EntityEmiStack(Registry.ENTITY_TYPE.get(new Identifier("minecraft:" + second)).create(MinecraftClient.getInstance().world));
            } else {
                return EmiStack.of(Registry.ITEM.get(new Identifier("minecraft:" + first.toLowerCase()))).copy().setAmount(Integer.parseInt(second));
            }
        }
    }

    private EmiStack getStackFromElement(JsonElement jsonElement, boolean empty) {
        final String id = jsonElement.getAsString();

        if (! id.equals("")) {
            return getStackFromId(id);
        } else if (empty) {
            return EmiStack.EMPTY;
        }

        return null;
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
            Utils.error(e);
        }
    }
}
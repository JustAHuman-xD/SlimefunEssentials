package me.justahuman.slimefunessentials.compatibility.emi;

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

import me.justahuman.slimefunessentials.Utils;
import me.justahuman.slimefunessentials.compatibility.emi.misc.Category;
import me.justahuman.slimefunessentials.compatibility.emi.misc.EntityEmiStack;
import me.justahuman.slimefunessentials.compatibility.emi.recipehandler.MultiblockHandler;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.AncientAltarRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.KillRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.MachineRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.MultiblockRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.OtherRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.SmelteryRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.ThreeByThreeRecipe;
import me.justahuman.slimefunessentials.compatibility.emi.recipetype.TradeRecipe;
import me.justahuman.slimefunessentials.config.ModConfig;

import me.shedaniel.autoconfig.AutoConfig;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmiIntegration implements EmiPlugin {
    //Data Processing Maps
    protected static final Set<String> specialCases = Collections.singleton("3X3_");
    protected static final Map<String, EmiRecipeCategory> categories = new HashMap<>();
    protected static final Map<EmiRecipeCategory, Set<EmiStack>> workstations = new HashMap<>();
    protected static final Map<String, EmiStack> recipeTypes = new HashMap<>();
    protected static final Map<String, EmiStack> multiblocks = new HashMap<>();
    protected static final LinkedHashMap<String, EmiStack> items = new LinkedHashMap<>();
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
        final JsonArray defaults = defaultObject.getAsJsonArray(modConfig.useMachineDefaults() ? "machines" : "multiblocks");
        final JsonArray otherDefaults = defaultObject.getAsJsonArray(! modConfig.useMachineDefaults() ? "machines" : "multiblocks");

        loadRecipes(emiRegistry, "core", defaults, otherDefaults);
        for (String addon : getEnabledAddons()) {
            loadRecipes(emiRegistry, addon, defaults, otherDefaults);
        }

        for (String itemId : items.keySet()) {
            emiRegistry.addEmiStack(items.get(itemId));
        }

        categories.clear();
        recipeTypes.clear();
        multiblocks.clear();
        items.clear();
    }

    private void loadRecipes(EmiRegistry emiRegistry, String sectionKey, JsonArray defaults, JsonArray otherDefaults) {
        final JsonObject sectionJson = jsonObject != null ? jsonObject.getAsJsonObject(sectionKey) : null;
        final JsonObject totalRecipes = sectionJson != null ? sectionJson.getAsJsonObject("recipes") : null;
        if (totalRecipes != null && defaults != null && otherDefaults != null) {
            final List<String> workstationList = new ArrayList<>(totalRecipes.keySet());
            workstationList.sort(Comparator.naturalOrder());
            for (String workstationId : workstationList) {

                final EmiStack workstationStack = getWorkstationStack(workstationId);
                final JsonArray recipes = totalRecipes.getAsJsonArray(workstationId);

                if (recipes == null || workstationStack == null) {
                    continue;
                }

                final String categoryId = "slimefunessentials:" + getCategoryId(workstationId).toLowerCase();
                final EmiRecipeCategory emiRecipeCategory;

                if (categories.containsKey(categoryId + workstationId)) {
                    emiRecipeCategory = categories.get(categoryId + workstationId);
                } else {
                    emiRecipeCategory = new Category(new Identifier(categoryId), workstationStack, workstationStack.getItemStack().getName());
                    emiRegistry.addCategory(emiRecipeCategory);
                    categories.put(categoryId + workstationId, emiRecipeCategory);
                }

                final Set<EmiStack> workstationSet = workstations.getOrDefault(emiRecipeCategory, new HashSet<>());
                if (! workstationStack.isEmpty() && ! workstationSet.contains(workstationStack)) {
                    workstationSet.add(workstationStack);
                    workstations.put(emiRecipeCategory, workstationSet);
                    emiRegistry.addWorkstation(emiRecipeCategory, workstationStack);
                }

                for (JsonElement recipeElement : recipes) {

                    if (! (recipeElement instanceof JsonObject recipe)) {
                        continue;
                    }

                    //Define important Variables
                    final List<EmiIngredient> inputs = new ArrayList<>();
                    final List<EmiStack> outputs = new ArrayList<>();
                    final StringBuilder uniqueId = new StringBuilder().append("slimefunessentials:/").append(workstationId.toLowerCase());
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
                            long amount = 0;
                            final List<EmiIngredient> multiples = new ArrayList<>();
                            for (JsonElement subJsonElement : jsonArray) {
                                final EmiStack input = (EmiStack) getStackFromElement(subJsonElement, false);
                                if (input != null) {
                                    amount = input.getAmount();
                                    input.setAmount(1);
                                    multiples.add(input);
                                }
                            }
                            inputs.add(EmiIngredient.of(multiples, amount));
                        } else {
                            final EmiIngredient input = getStackFromElement(jsonElement, true);
                            if (input != null) {
                                inputs.add(input);
                            }
                        }
                    }

                    //Fill Outputs
                    for (JsonElement jsonElement : recipeOutputs) {
                        final EmiStack output = (EmiStack) getStackFromElement(jsonElement, false);
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

    private String getCategoryId(String workstationId) {
        if (multiblocks.containsKey(workstationId)) {
            return "multiblock";
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
        } else if (workstationId.contains("MULTIBLOCK")) {
            return "multiblock";
        } else if (workstationId.contains("3X3")) {
            return "3by3";
        } else if (items.containsKey(workstationId) || recipeTypes.containsKey(workstationId)) {
            return "machine";
        }
        return "error";
    }

    private EmiStack getWorkstationStack(String workstationId) {
        for (String specialCase : specialCases) {
            if (workstationId.contains(specialCase)) {
                workstationId = workstationId.substring(workstationId.indexOf(specialCase) + specialCase.length());
            }
        }
        if (multiblocks.containsKey(workstationId)) {
            return multiblocks.get(workstationId);
        } else if (items.containsKey(workstationId)) {
            return items.get(workstationId);
        } else if (recipeTypes.containsKey(workstationId)) {
            return recipeTypes.get(workstationId);
        }
        return new EmptyEmiStack();
    }

    private EmiIngredient getStackFromId(String id) {
        final String first = id.substring(0, id.indexOf(":"));
        final String second = id.substring(id.indexOf(":") + 1);
        if (items.containsKey(first)) {
            return items.get(first).comparison(original -> original.copy().nbt(true).build()).copy().setAmount(Integer.parseInt(second));
        } else if (multiblocks.containsKey(first)) {
            return multiblocks.get(first).comparison(original -> original.copy().nbt(true).build()).copy().setAmount(Integer.parseInt(second));
        } else {
            if (first.equals("entity")) {
                return new EntityEmiStack(Registry.ENTITY_TYPE.get(new Identifier("minecraft:" + second)).create(MinecraftClient.getInstance().world));
            } else if (first.equals("fluid")) {
                return EmiStack.of(Registry.FLUID.get(new Identifier("minecraft:" + second)));
            } else if (first.contains("#")) {
                final TagKey<Item> tagKey = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft", first.substring(1)));
                return EmiIngredient.of(tagKey);
            } else {
                return EmiStack.of(Registry.ITEM.get(new Identifier("minecraft:" + first.toLowerCase()))).copy().setAmount(Integer.parseInt(second));
            }
        }
    }

    private EmiIngredient getStackFromElement(JsonElement jsonElement, boolean empty) {
        final String id = jsonElement.getAsString();

        if (! id.equals("")) {
            return getStackFromId(id);
        } else if (empty) {
            return EmiStack.EMPTY;
        }

        return null;
    }

    private void loadSection(String sectionKey) {
        try {
            final JsonObject sectionJson = jsonObject.getAsJsonObject(sectionKey);

            if (sectionJson == null) {
                return;
            }

            final JsonObject itemsJson = sectionJson.getAsJsonObject("items");
            final JsonObject recipeTypeJson = sectionJson.getAsJsonObject("recipe_types");
            final JsonObject multiblockJson = sectionJson.getAsJsonObject("multiblocks");

            if (itemsJson != null && ! itemsJson.keySet().isEmpty()) {
                final List<String> itemList = new ArrayList<>(itemsJson.keySet());
                itemList.sort(Comparator.naturalOrder());
                for (String slimefunId : itemList) {
                    final JsonObject object = itemsJson.getAsJsonObject(slimefunId);
                    if (object != null) {
                        items.put(slimefunId, (EmiStack) EmiStackSerializer.deserialize(object));
                    }
                }
                itemList.clear();
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

    private void loadItems() {
        if (jsonObject == null) {
            return;
        }

        loadSection("core");
        for (String addon : getEnabledAddons()) {
            loadSection(addon);
        }
    }

    private List<String> getEnabledAddons() {
        final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        final List<String> addonList = new ArrayList<>();

        if (modConfig.enableSpiritsUnchained()) {
            addonList.add("spirits_unchained");
        }

        if (modConfig.enableElectricSpawners()) {
            addonList.add("electric_spawners");
        }

        if (modConfig.enableEcoPower()) {
            addonList.add("eco_power");
        }

        if (modConfig.enableExoticGarden()) {
            addonList.add("exotic_garden");
        }

        if (modConfig.enableHotbarPets()) {
            addonList.add("hotbar_pets");
        }

        if (modConfig.enableExtraGear()) {
            addonList.add("extra_gear");
        }

        if (modConfig.enableLuckyBlocks()) {
            addonList.add("lucky_blocks");
        }

        if (modConfig.enableSoulJars()) {
            addonList.add("soul_jars");
        }

        if (modConfig.enableSlimyTreeTaps()) {
            addonList.add("slimy_tree_taps");
        }

        if (modConfig.enableDankTech()) {
            addonList.add("dank_tech");
        }

        if (modConfig.enableTransCendence()) {
            addonList.add("transcendence");
        }

        return addonList;
    }
}

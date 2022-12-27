package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.core.Utils;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecipeLoader {
    // Storage
    @Getter
    protected static final Map<String, ConditionContainer> conditionMap = new HashMap<>(Map.of(
            "day", new ConditionContainer(Utils.WIDGETS, "day", 57, 0, 13, 13),
            "night", new ConditionContainer(Utils.WIDGETS, "night", 70, 0, 13, 13),
            "obstructed", new ConditionContainer(Utils.WIDGETS, "obstructed", 83, 0, 13, 13),
            "waterlogged", new ConditionContainer(Utils.WIDGETS, "waterlogged", 96, 0, 13, 13),
            "overworld", new ConditionContainer(Utils.WIDGETS, "overworld", 109, 0, 13, 13),
            "nether", new ConditionContainer(Utils.WIDGETS, "nether", 122, 0, 13, 13),
            "end", new ConditionContainer(Utils.WIDGETS, "end", 135, 0, 13, 13)
    ));
    @Getter
    protected static final LinkedHashMap<String, TypeStack> items = new LinkedHashMap<>();
    @Getter
    protected static final Map<String, TypeStack> recipeTypes = new HashMap<>();
    @Getter
    protected static final Map<String, TypeStack> multiblocks = new HashMap<>();
    @Getter
    protected static final Map<String, CategoryContainer> categories = new HashMap<>();

    // Loading
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.json");
    private static final JsonObject jsonObject = dataStream != null ? GSON.fromJson(new InputStreamReader(dataStream), JsonObject.class) : null;


    public static void load() {
        if (jsonObject == null || ! items.isEmpty()) {
            return;
        }

        final List<String> enabledAddons = getEnabledAddons();
        for (String addon : enabledAddons) {
            loadItems(addon);
        }

        for (String addon : enabledAddons) {
            loadRecipes(addon);
        }
    }

    public static void clear() {
        items.clear();
        recipeTypes.clear();
        multiblocks.clear();
        categories.clear();
    }

    private static void loadItems(String section) {
        final JsonObject sectionObject = jsonObject.getAsJsonObject(section);

        if (sectionObject == null) {
            return;
        }

        final JsonObject itemsObject = sectionObject.getAsJsonObject("items");
        final JsonObject multiblocksObject = sectionObject.getAsJsonObject("multiblocks");
        final JsonObject recipeTypesObject = sectionObject.getAsJsonObject("recipe_types");

        if (itemsObject != null && ! itemsObject.keySet().isEmpty()) {
            final List<String> itemIds = new ArrayList<>(itemsObject.keySet());
            itemIds.sort(Comparator.naturalOrder());
            for (String slimefunId : itemIds) {
                final JsonObject item = itemsObject.getAsJsonObject(slimefunId);
                if (item != null) {
                    items.put(slimefunId, new TypeStack(getType(item), deserializeItem(item)));
                }
            }
        }

        if (multiblocksObject != null && ! multiblocksObject.keySet().isEmpty()) {
            for (String multiblockId : multiblocksObject.keySet()) {
                final JsonObject multiblock = multiblocksObject.getAsJsonObject(multiblockId);
                if (multiblock != null) {
                    multiblocks.put(multiblockId, new TypeStack(getType(multiblock), deserializeItem(multiblock)));
                }
            }
        }

        if (recipeTypesObject != null && ! recipeTypesObject.keySet().isEmpty()) {
            for (String recipeTypeId : recipeTypesObject.keySet()) {
                final JsonObject recipeType = recipeTypesObject.getAsJsonObject(recipeTypeId);
                if (recipeType != null) {
                    recipeTypes.put(recipeTypeId, new TypeStack(getType(recipeType), deserializeItem(recipeType)));
                }
            }
        }
    }

    private static void loadRecipes(String section) {
        final JsonObject sectionJson = jsonObject != null ? jsonObject.getAsJsonObject(section) : null;
        final JsonObject totalRecipes = sectionJson != null ? sectionJson.getAsJsonObject("recipes") : null;
        if (totalRecipes != null) {
            final List<String> workstationList = new ArrayList<>(totalRecipes.keySet());
            workstationList.sort(Comparator.naturalOrder());
            for (String workstationId : workstationList) {

                final ItemStack workstationStack = getWorkstationStack(workstationId);
                final JsonArray recipes = totalRecipes.getAsJsonArray(workstationId);

                if (recipes == null || workstationStack == null) {
                    continue;
                }

                final String categoryId = "slimefun_essentials:" + getCategoryId(workstationId).toLowerCase();
                final CategoryContainer categoryContainer;

                if (categories.containsKey(categoryId + workstationId)) {
                    categoryContainer = categories.get(categoryId + workstationId);
                } else {
                    categoryContainer = new CategoryContainer(new Identifier(categoryId), workstationStack, new ArrayList<>());
                    categories.put(categoryId + workstationId, categoryContainer);
                }

                for (JsonElement recipeElement : recipes) {

                    if (! (recipeElement instanceof JsonObject recipe)) {
                        continue;
                    }

                    //Define important Variables
                    final List<CustomMultiStack> inputs = new ArrayList<>();
                    final List<CustomMultiStack> outputs = new ArrayList<>();
                    final List<ConditionContainer> conditions = new ArrayList<>();
                    final StringBuilder uniqueId = new StringBuilder().append("slimefun_essentials:/").append(clearConditions(workstationId).toLowerCase());
                    final JsonArray recipeInputs = recipe.getAsJsonArray("inputs") != null ? recipe.getAsJsonArray("inputs") : new JsonArray();
                    final JsonArray recipeOutputs = recipe.getAsJsonArray("outputs")!= null ? recipe.getAsJsonArray("outputs") : new JsonArray();
                    final JsonArray recipeConditions = recipe.getAsJsonArray("conditions")!= null ? recipe.getAsJsonArray("conditions") : new JsonArray();
                    final Integer ticks = recipe.get("time") != null ? recipe.get("time").getAsInt() : 0;
                    final Integer energy = recipe.get("energy") != null ? recipe.get("energy").getAsInt() : 0;

                    if (recipeInputs == null || recipeOutputs == null) {
                        continue;
                    }

                    //Fill Inputs
                    for (JsonElement jsonElement : recipeInputs) {
                        long amount = 0;
                        if (jsonElement instanceof JsonArray jsonArray) {
                            final List<Object> multiples = new ArrayList<>();
                            for (JsonElement subJsonElement : jsonArray) {
                                final Object input = getStackFromElement(subJsonElement, false);
                                if (input != null) {
                                    if (input instanceof ItemStack itemStack) {
                                        amount = itemStack.getCount();
                                        itemStack.setCount(1);
                                    }
                                    multiples.add(input);
                                }
                            }
                            inputs.add(new CustomMultiStack(multiples, amount));
                        } else {
                            final Object input = getStackFromElement(jsonElement, true);
                            if (input != null) {
                                if (input instanceof ItemStack itemStack) {
                                    amount = itemStack.getCount();
                                }
                                inputs.add(new CustomMultiStack(Collections.singletonList(input), amount));
                            }
                        }
                    }

                    //Fill Outputs
                    for (JsonElement jsonElement : recipeOutputs) {
                        long amount = 0;
                        final Object output = getStackFromElement(jsonElement, false);
                        if (output != null) {
                            if (output instanceof ItemStack itemStack) {
                                amount = itemStack.getCount();
                                itemStack.setCount(1);
                            }
                            outputs.add(new CustomMultiStack(Collections.singletonList(output), amount));
                        }
                    }
                    
                    //Fill Conditions
                    
                    //Global Workstation Conditions
                    for (String conditionKey : conditionMap.keySet()) {
                        final String globalKey = "[" + conditionKey.toUpperCase() + "]";
                        if (workstationId.contains(globalKey)) {
                            conditions.add(conditionMap.get(conditionKey));
                        }
                    }
                    
                    //Per Recipe Conditions
                    for (JsonElement jsonElement : recipeConditions) {
                        final String condition = jsonElement.getAsString();
                        conditions.add(conditionMap.getOrDefault(condition, new ConditionContainer(Utils.WIDGETS, "error", 0, 243, 13, 13)));
                    }

                    //Remove any parts of the String that cannot be an Identifier
                    for (char x : recipe.toString().toLowerCase().toCharArray()) {
                        if (Character.isLetterOrDigit(x)) {
                            uniqueId.append(x);
                        }
                    }

                    final RecipeContainer recipeContainer = new RecipeContainer(new Identifier(uniqueId.toString()), getCategoryId(workstationId),inputs, outputs, ticks, energy, conditions);

                    if (! categoryContainer.getRecipes().contains(recipeContainer)) {
                        final List<RecipeContainer> recipeContainers = categoryContainer.getRecipes();
                        recipeContainers.add(recipeContainer);
                        categoryContainer.setRecipes(recipeContainers);
                    }
                }
            }
        }
    }

    private static List<String> getEnabledAddons() {
        final List<String> addonList = new ArrayList<>(Collections.singletonList("core"));
        if (Utils.isClothConfigEnabled()) {
            final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            
            if (modConfig.isElectricSpawnersEnabled()) {
                addonList.add("electric_spawners");
            }
            if (modConfig.isExtraGearEnabled()) {
                addonList.add("extra_gear");
            }
            if (modConfig.isEcoPowerEnabled()) {
                addonList.add("eco_power");
            }
            if (modConfig.isSoulJarsEnabled()) {
                addonList.add("soul_jars");
            }
            if (modConfig.isTranscendenceEnabled()) {
                addonList.add("transcendence");
            }
            if (modConfig.isSpiritsUnchainedEnabled()) {
                addonList.add("spirits_unchained");
            }
            if (modConfig.isHotbarPetsEnabled()) {
                addonList.add("hotbar_pets");
            }
            if (modConfig.isDankTech2Enabled()) {
                addonList.add("dank_tech_2");
            }
            if (modConfig.isLuckyBlocksEnabled()) {
                addonList.add("lucky_blocks");
            }
            if (modConfig.isSlimyTreeTapsEnabled()) {
                addonList.add("slimy_tree_taps");
            }
            if (modConfig.isExoticGardenEnabled()) {
                addonList.add("exotic_garden");
            }
            if (modConfig.isInfinityExpansionEnabled()) {
                addonList.add("infinity_expansion");
            }
            if (modConfig.isGlobalWarmingEnabled()) {
                addonList.add("global_warming");
            }
            if (modConfig.isMiniblocksEnabled()) {
                addonList.add("miniblocks");
            }
            if (modConfig.isNetworksEnabled()) {
                addonList.add("networks");
            }
            if (modConfig.isCrystamaeHistoriaEnabled()) {
                addonList.add("crystamae_historia");
            }
            if (modConfig.isExtraToolsEnabled()) {
                addonList.add("extra_tools");
            }
            if (modConfig.isFoxyMachinesEnabled()) {
                addonList.add("foxy_machines");
            }
            if (modConfig.isRelicsOfCthoniaEnabled()) {
                addonList.add("relics_of_cthonia");
            }
            if (modConfig.isFnAmplificationsEnabled()) {
                addonList.add("fn_amplifications");
            }
            if (modConfig.isSimpleMaterialGeneratorsEnabled()) {
                addonList.add("simple_material_generators");
            }
            if (modConfig.isDynaTechEnabled()) {
                addonList.add("dyna_tech");
            }
            if (modConfig.isDyeBenchEnabled()) {
                addonList.add("dye_bench");
            }
            if (modConfig.isSupremeEnabled()) {
                addonList.add("supreme");
            }
            if (modConfig.isWilderNetherEnabled()) {
                addonList.add("wilder_nether");
            }
            if (modConfig.isCompressionCraftEnabled()) {
                addonList.add("compression_craft");
            }
            if (modConfig.isVillagerUtilEnabled()) {
                addonList.add("villager_util");
            }
            if (modConfig.isMobCapturerEnabled()) {
                addonList.add("mob_capturer");
            }
            if (modConfig.isEmcTechEnabled()) {
                addonList.add("emc_tech");
            }
            if (modConfig.isFlowerTechEnabled()) {
                addonList.add("flower_power");
            }
            if (modConfig.isSlimeTinkerEnabled()) {
                addonList.add("slime_tinker");
            }
            if (modConfig.isGalactifunEnabled()) {
                addonList.add("galactifun");
            }
            if (modConfig.isFluffyMachinesEnabled()) {
                addonList.add("fluffy_machines");
            }
            if (modConfig.isAlchimiaVitaeEnabled()) {
                addonList.add("alchimia_vitae");
            }
            if (modConfig.isSlimefunWarfareEnabled()) {
                addonList.add("slimefun_warfare");
            }
            if (modConfig.isLiquidEnabled()) {
                addonList.add("liquid");
            }
            if (modConfig.isLiteXpansionEnabled()) {
                addonList.add("lite_xpansion");
            }
            if (modConfig.isSimpleUtilsEnabled()) {
                addonList.add("simple_utils");
            }
            if (modConfig.isElementManipulationEnabled()) {
                addonList.add("element_manipulation");
            }
            if (modConfig.isNetheopoiesisEnabled()) {
                addonList.add("netheopoiesis");
            }
            if (modConfig.isSimpleStorageEnabled()) {
                addonList.add("simple_storage");
            }
            if (modConfig.isSlimyRepairEnabled()) {
                addonList.add("slimy_repair");
            }
        }

        return addonList;
    }
    
    private static String clearConditions(String toClear) {
        for (String conditionId : conditionMap.keySet()) {
            conditionId = "[" + conditionId.toUpperCase() + "]";
            if (toClear.contains(conditionId)) {
                toClear = toClear.replace(conditionId, "");
            }
        }
        return toClear;
    }

    private static String getCategoryId(String workstationId) {
        workstationId = clearConditions(workstationId);
        
        if (multiblocks.containsKey(workstationId)) {
            return "multiblock";
        } else if (items.containsKey(workstationId)) {
            return items.get(workstationId).type();
        } else if (recipeTypes.containsKey(workstationId)) {
            return recipeTypes.get(workstationId).type();
        }
        Utils.log(workstationId);
        return "error";
    }

    private static ItemStack getWorkstationStack(String workstationId) {
        workstationId = clearConditions(workstationId);
        
        if (multiblocks.containsKey(workstationId)) {
            return multiblocks.get(workstationId).itemStack();
        } else if (items.containsKey(workstationId)) {
            return items.get(workstationId).itemStack();
        } else if (recipeTypes.containsKey(workstationId)) {
            return recipeTypes.get(workstationId).itemStack();
        }
        
        return new ItemStack(Items.BARRIER);
    }

    private static Object getStackFromId(String id) {
        final String first = id.substring(0, id.indexOf(":"));
        final String second = id.substring(id.indexOf(":") + 1);
        if (items.containsKey(first)) {
            final ItemStack item = items.get(first).itemStack().copy();
            item.setCount(Integer.parseInt(second));
            return item;
        } else if (multiblocks.containsKey(first)) {
            final ItemStack item = multiblocks.get(first).itemStack().copy();
            item.setCount(Integer.parseInt(second));
            return item;
        }
        else {
            if (first.equals("entity")) {
                return Registry.ENTITY_TYPE.get(new Identifier("minecraft:" + second)).create(MinecraftClient.getInstance().world);
            } else if (first.equals("fluid")) {
                return Registry.FLUID.get(new Identifier("minecraft:" + second));
            } else if (first.contains("#")) {
                return new TagStack(TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft", first.substring(1))), Integer.parseInt(second));
            } else {
                final ItemStack item = Registry.ITEM.get(new Identifier("minecraft:" + first.toLowerCase())).getDefaultStack().copy();
                item.setCount(Integer.parseInt(second));
                return item;
            }
        }
    }

    private static Object getStackFromElement(JsonElement jsonElement, boolean empty) {
        final String id = jsonElement.getAsString();

        if (! id.equals("")) {
            return getStackFromId(id);
        } else if (empty) {
            return new ItemStack(Items.AIR);
        }

        return null;
    }
    
    private static String getType(JsonObject itemObject) {
        return JsonHelper.hasString(itemObject, "type") ? itemObject.get("type").getAsString() : "process";
    }

    private static ItemStack deserializeItem(JsonObject itemObject) {
        final ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(itemObject.get("item").getAsString())));
        itemStack.setCount(JsonHelper.getInt(itemObject, "amount", 1));
        if (JsonHelper.hasString(itemObject, "nbt")) {
            itemStack.setNbt(parseNbt(itemObject));
        }

        return itemStack;
    }

    private static NbtCompound parseNbt(JsonObject json) {
        try {
            return StringNbtReader.parse(JsonHelper.getString(json, "nbt"));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            "end", new ConditionContainer(Utils.WIDGETS, "end", 135, 0, 13, 13),
            "place", new ConditionContainer(Utils.WIDGETS, "place", 148, 0, 13, 13),
            "dragon_egg", new ConditionContainer(Utils.WIDGETS, "dragon_egg", 161, 0, 13, 13)
    ));
    @Getter
    protected static final LinkedHashMap<String, TypeStack> items = new LinkedHashMap<>();
    @Getter
    protected static final Map<String, TypeStack> placeholders = new HashMap<>();
    @Getter
    protected static final Map<String, TypeStack> recipeTypes = new HashMap<>();
    @Getter
    protected static final Map<String, TypeStack> multiblocks = new HashMap<>();
    @Getter
    protected static final Map<String, CategoryContainer> categories = new HashMap<>();
    @Getter
    protected static final Map<String, CopyContainer> toCopy = new HashMap<>();

    // Loading
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.json");
    private static final JsonObject jsonObject = dataStream != null ? GSON.fromJson(new InputStreamReader(dataStream, StandardCharsets.UTF_8), JsonObject.class) : null;
    
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
        
        for (String copyToId : toCopy.keySet()) {
            Utils.log(copyToId);
            final CopyContainer copyContainer = toCopy.get(copyToId);
            final String copyFromId = copyContainer.copyFromId();
            final String inputChange = copyContainer.inputs();
            final String outputChange = copyContainer.outputs();
            final String timeChange = copyContainer.time();
            
            final CategoryContainer copyToContainer = categories.get(copyToId);
            final CategoryContainer copyFromContainer = categories.get(copyFromId);
            final List<RecipeContainer> newRecipes = new ArrayList<>();
            final List<RecipeContainer> copyRecipes = copyFromContainer.getRecipes();
            
            for (RecipeContainer recipeContainer : copyRecipes) {
                final String other = recipeContainer.id().getPath().replace("/" + clearConditions(copyFromId).toLowerCase(), "");
                final String type = recipeContainer.type();
                final List<CustomMultiStack> inputs = change(inputChange, recipeContainer.inputs());
                final List<CustomMultiStack> outputs = change(outputChange, recipeContainer.outputs());
                final List<ConditionContainer> conditions = recipeContainer.conditions();
                final float time = change(timeChange, recipeContainer.time());
                final int energy = copyToContainer.getWorkstation().energy();
                final Identifier uniqueId = new Identifier("slimefun_essentials:/" + clearConditions(copyToId).toLowerCase() + other);
                newRecipes.add(new RecipeContainer(uniqueId, type, inputs, outputs, time, energy, conditions));
            }
            copyToContainer.setRecipes(newRecipes);
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
        final JsonObject placeholdersObject = sectionObject.getAsJsonObject("placeholders");

        if (itemsObject != null && ! itemsObject.keySet().isEmpty()) {
            final List<String> itemIds = new ArrayList<>(itemsObject.keySet());
            itemIds.sort(Comparator.naturalOrder());
            for (String slimefunId : itemIds) {
                final JsonObject item = itemsObject.getAsJsonObject(slimefunId);
                if (item != null) {
                    items.put(slimefunId, new TypeStack(getType(item, "process"), deserializeItem(item)));
                }
            }
        }

        if (multiblocksObject != null && ! multiblocksObject.keySet().isEmpty()) {
            for (String multiblockId : multiblocksObject.keySet()) {
                final JsonObject multiblock = multiblocksObject.getAsJsonObject(multiblockId);
                if (multiblock != null) {
                    multiblocks.put(multiblockId, new TypeStack(getType(multiblock, "multiblock"), deserializeItem(multiblock)));
                }
            }
        }

        if (recipeTypesObject != null && ! recipeTypesObject.keySet().isEmpty()) {
            for (String recipeTypeId : recipeTypesObject.keySet()) {
                final JsonObject recipeType = recipeTypesObject.getAsJsonObject(recipeTypeId);
                if (recipeType != null) {
                    recipeTypes.put(recipeTypeId, new TypeStack(getType(recipeType, "process"), deserializeItem(recipeType)));
                }
            }
        }
    
        if (placeholdersObject != null && ! placeholdersObject.keySet().isEmpty()) {
            for (String placeholderId : placeholdersObject.keySet()) {
                final JsonObject placeholder = placeholdersObject.getAsJsonObject(placeholderId);
                if (placeholder != null) {
                    placeholders.put(placeholderId, new TypeStack(getType(placeholder, "process"), deserializeItem(placeholder)));
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
                final JsonElement workstationRecipes = totalRecipes.get(workstationId);

                if (workstationRecipes == null || workstationStack == null) {
                    continue;
                }

                final String categoryId = "slimefun_essentials:" + getCategoryId(workstationId).toLowerCase();
                final boolean existingContainer = categories.containsKey(workstationId);
                CategoryContainer categoryContainer = existingContainer ? categories.get(workstationId) : new CategoryContainer(new Identifier(categoryId), new Workstation(workstationStack, 0), new ArrayList<>());
                
                if (workstationRecipes instanceof JsonObject workstationObject) {
                    final int energy = existingContainer ? categoryContainer.getWorkstation().energy() : workstationObject.get("energy").getAsInt();
                    if (!existingContainer) {
                        categoryContainer = new CategoryContainer(new Identifier(categoryId), new Workstation(workstationStack, energy), new ArrayList<>());
                    }
                    
                    if (workstationObject.has("copy")) {
                        final String copyFromId = workstationObject.get("copy").getAsString();
                        final String inputs = workstationObject.has("inputs") ? workstationObject.get("inputs").getAsString() : null;
                        final String outputs = workstationObject.has("outputs") ? workstationObject.get("outputs").getAsString() : null;
                        final String time = workstationObject.has("time") ? workstationObject.get("time").getAsString() : null;
                        toCopy.put(workstationId, new CopyContainer(copyFromId, inputs, outputs, time));
                        categories.put(workstationId, categoryContainer);
                        continue;
                    }
                    
                    final JsonArray workstationArray = workstationObject.getAsJsonArray("recipes");
                    for (JsonElement recipeElement : workstationArray) {
                        if (! (recipeElement instanceof JsonObject recipe)) {
                            continue;
                        }
    
                        final StringBuilder uniqueId = new StringBuilder().append("slimefun_essentials:/").append(clearConditions(workstationId).toLowerCase());
                        final JsonArray recipeInputs = recipe.getAsJsonArray("inputs") != null ? recipe.getAsJsonArray("inputs") : new JsonArray();
                        final JsonArray recipeOutputs = recipe.getAsJsonArray("outputs")!= null ? recipe.getAsJsonArray("outputs") : new JsonArray();
                        final JsonArray recipeConditions = recipe.getAsJsonArray("conditions")!= null ? recipe.getAsJsonArray("conditions") : new JsonArray();
                        final int time = recipe.get("time").getAsInt();
                        
                        handleRecipe(categoryContainer, workstationId, uniqueId, recipe.toString().toLowerCase(), recipeInputs, recipeOutputs, recipeConditions, time, energy);
                    }
                } else if (workstationRecipes instanceof JsonArray workstationArray) {
                    for (JsonElement recipeElement : workstationArray) {
                        if (! (recipeElement instanceof JsonObject recipe)) {
                            continue;
                        }
                        
                        final StringBuilder uniqueId = new StringBuilder().append("slimefun_essentials:/").append(clearConditions(workstationId).toLowerCase());
                        final JsonArray recipeInputs = recipe.getAsJsonArray("inputs") != null ? recipe.getAsJsonArray("inputs") : new JsonArray();
                        final JsonArray recipeOutputs = recipe.getAsJsonArray("outputs")!= null ? recipe.getAsJsonArray("outputs") : new JsonArray();
                        final JsonArray recipeConditions = recipe.getAsJsonArray("conditions")!= null ? recipe.getAsJsonArray("conditions") : new JsonArray();
                        final float time = recipe.get("time") != null ? recipe.get("time").getAsInt() / 20F : 0;
                        final int energy = recipe.get("energy") != null ? (int) (recipe.get("energy").getAsInt() / time) : 0;
                        handleRecipe(categoryContainer, workstationId, uniqueId, recipe.toString().toLowerCase(), recipeInputs, recipeOutputs, recipeConditions, time, energy);
                    }
                }
    
                categories.put(workstationId, categoryContainer);
            }
        }
    }
    
    private static void handleRecipe(CategoryContainer categoryContainer, String workstationId, StringBuilder uniqueId, String toAddToId, JsonArray recipeInputs, JsonArray recipeOutputs, JsonArray recipeConditions, float time, int energy) {
        //Define important Variables
        final List<CustomMultiStack> inputs = new ArrayList<>();
        final List<CustomMultiStack> outputs = new ArrayList<>();
        final List<ConditionContainer> conditions = new ArrayList<>();
        
        if (recipeInputs == null || recipeOutputs == null) {
            return;
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
        for (char x : toAddToId.toCharArray()) {
            if (Character.isLetterOrDigit(x)) {
                uniqueId.append(x);
            }
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
                        if (input instanceof Collection<?> collection) {
                            multiples.addAll(collection);
                        } else {
                            multiples.add(input);
                        }
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
            if (jsonElement instanceof JsonArray outputArray) {
                int i = 0;
                for (JsonElement subJsonElement : outputArray) {
                    uniqueId.append(i);
                    final List<CustomMultiStack> subOutputs = new ArrayList<>();
                    final Object output = getStackFromElement(subJsonElement, false);
                    if (output != null) {
                        if (output instanceof ItemStack itemStack) {
                            amount = itemStack.getCount();
                            itemStack.setCount(1);
                        }
                        subOutputs.add(new CustomMultiStack(Collections.singletonList(output), amount));
                        registerRecipe(categoryContainer, uniqueId, getCategoryId(workstationId), inputs, subOutputs, time, energy, conditions);
                    }
                    i++;
                }
                return;
            } else {
                final Object output = getStackFromElement(jsonElement, false);
                if (output instanceof ItemStack itemStack) {
                    amount = itemStack.getCount();
                    itemStack.setCount(1);
                }
                outputs.add(new CustomMultiStack(Collections.singletonList(output), amount));
            }
        }
    
        registerRecipe(categoryContainer, uniqueId, getCategoryId(workstationId), inputs, outputs, time, energy, conditions);
    }
    
    private static void registerRecipe(CategoryContainer categoryContainer, StringBuilder uniqueId, String categoryId, List<CustomMultiStack>inputs, List<CustomMultiStack> outputs, float time, int energy, List<ConditionContainer> conditions) {
        final RecipeContainer recipeContainer = new RecipeContainer(new Identifier(uniqueId.toString()), categoryId,inputs, outputs, time, energy, conditions);
    
        if (! categoryContainer.getRecipes().contains(recipeContainer)) {
            final List<RecipeContainer> recipeContainers = categoryContainer.getRecipes();
            recipeContainers.add(recipeContainer);
            categoryContainer.setRecipes(recipeContainers);
        }
    }

    private static List<String> getEnabledAddons() {
        final List<String> addonList = new ArrayList<>(Collections.singletonList("slimefun"));
        if (Utils.isClothConfigEnabled()) {
            final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    
            if (modConfig.isAlchimiaVitaeEnabled()) {
                addonList.add("alchimia_vitae");
            }
            if (modConfig.isCompressionCraftEnabled()) {
                addonList.add("compression_craft");
            }
            if (modConfig.isCrystamaeHistoriaEnabled()) {
                addonList.add("crystamae_historia");
            }
            if (modConfig.isDankTech2Enabled()) {
                addonList.add("dank_tech_2");
            }
            if (modConfig.isDyeBenchEnabled()) {
                addonList.add("dye_bench");
            }
            if (modConfig.isDynaTechEnabled()) {
                addonList.add("dyna_tech");
            }
            if (modConfig.isEcoPowerEnabled()) {
                addonList.add("eco_power");
            }
            if (modConfig.isElectricSpawnersEnabled()) {
                addonList.add("electric_spawners");
            }
            if (modConfig.isElementManipulationEnabled()) {
                addonList.add("element_manipulation");
            }
            if (modConfig.isEmcTechEnabled()) {
                addonList.add("emc_tech");
            }
            if (modConfig.isExoticGardenEnabled()) {
                addonList.add("exotic_garden");
            }
            if (modConfig.isExtraGearEnabled()) {
                addonList.add("extra_gear");
            }
            if (modConfig.isExtraToolsEnabled()) {
                addonList.add("extra_tools");
            }
            if (modConfig.isFlowerPowerEnabled()) {
                addonList.add("flower_power");
            }
            if (modConfig.isFluffyMachinesEnabled()) {
                addonList.add("fluffy_machines");
            }
            if (modConfig.isFnAmplificationsEnabled()) {
                addonList.add("fn_amplifications");
            }
            if (modConfig.isFoxyMachinesEnabled()) {
                addonList.add("foxy_machines");
            }
            if (modConfig.isGalactifunEnabled()) {
                addonList.add("galactifun");
            }
            if (modConfig.isGlobalWarmingEnabled()) {
                addonList.add("global_warming");
            }
            if (modConfig.isHotbarPetsEnabled()) {
                addonList.add("hotbar_pets");
            }
            if (modConfig.isInfinityExpansionEnabled()) {
                addonList.add("infinity_expansion");
            }
            if (modConfig.isLiquidEnabled()) {
                addonList.add("liquid");
            }
            if (modConfig.isLiteXpansionEnabled()) {
                addonList.add("lite_xpansion");
            }
            if (modConfig.isLuckyBlocksEnabled()) {
                addonList.add("lucky_blocks");
            }
            if (modConfig.isMiniblocksEnabled()) {
                addonList.add("miniblocks");
            }
            if (modConfig.isMobCapturerEnabled()) {
                addonList.add("mob_capturer");
            }
            if (modConfig.isNetheopoiesisEnabled()) {
                addonList.add("netheopoiesis");
            }
            if (modConfig.isNetworksEnabled()) {
                addonList.add("networks");
            }
            if (modConfig.isRelicsOfCthoniaEnabled()) {
                addonList.add("relics_of_cthonia");
            }
            if (modConfig.isSimpleMaterialGeneratorsEnabled()) {
                addonList.add("simple_material_generators");
            }
            if (modConfig.isSimpleStorageEnabled()) {
                addonList.add("simple_storage");
            }
            if (modConfig.isSimpleUtilsEnabled()) {
                addonList.add("simple_utils");
            }
            if (modConfig.isSlimeTinkerEnabled()) {
                addonList.add("slime_tinker");
            }
            if (modConfig.isSlimefunWarfareEnabled()) {
                addonList.add("slimefun_warfare");
            }
            if (modConfig.isSlimyRepairEnabled()) {
                addonList.add("slimy_repair");
            }
            if (modConfig.isSlimyTreeTapsEnabled()) {
                addonList.add("slimy_tree_taps");
            }
            if (modConfig.isSoulJarsEnabled()) {
                addonList.add("soul_jars");
            }
            if (modConfig.isSpiritsUnchainedEnabled()) {
                addonList.add("spirits_unchained");
            }
            if (modConfig.isSupremeEnabled()) {
                addonList.add("supreme");
            }
            if (modConfig.isTranscendenceEnabled()) {
                addonList.add("transcendence");
            }
            if (modConfig.isVillagerUtilEnabled()) {
                addonList.add("villager_util");
            }
            if (modConfig.isWilderNetherEnabled()) {
                addonList.add("wilder_nether");
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
            return multiblocks.get(workstationId).type();
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
        String first;
        String second;
        try {
            first = id.substring(0, id.indexOf(":"));
            second = id.substring(id.indexOf(":") + 1);
        } catch (IndexOutOfBoundsException ignored) {
            Utils.log("Invalid Stack Id: " + id);
            first = "barrier";
            second = "1";
        }
        if (items.containsKey(first)) {
            final ItemStack item = items.get(first).itemStack().copy();
            item.setCount(Integer.parseInt(second));
            return item;
        } else if (placeholders.containsKey(first)) {
            final ItemStack item = placeholders.get(first).itemStack().copy();
            item.setCount(Integer.parseInt(second));
            return item;
        } else if (multiblocks.containsKey(first)) {
            final ItemStack item = multiblocks.get(first).itemStack().copy();
            item.setCount(Integer.parseInt(second));
            return item;
        } else {
            if (first.equals("entity")) {
                if (second.equals("all")) {
                    final Set<Entity> entities = new HashSet<>();
                    for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
                        if (entityType.create(MinecraftClient.getInstance().world) instanceof LivingEntity) {
                            entities.add(entityType.create(MinecraftClient.getInstance().world));
                        }
                    }
                    return entities;
                }
                if (second.equals("player")) {
                    return EntityType.PLAYER.create(MinecraftClient.getInstance().world);
                }
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
    
    private static String getType(JsonObject itemObject, String defaultType) {
        return JsonHelper.hasString(itemObject, "type") ? itemObject.get("type").getAsString() : defaultType;
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
    
    private static float change(String changeBy, float toChange) {
        if (changeBy == null) {
            return toChange;
        }
        final String changeWith = changeBy.substring(0, 1);
        final int changeAmount = Integer.parseInt(changeBy.substring(1));
        if (changeWith.equals("/")) {
            return (((int) toChange * 2 / changeAmount) / 2F);
        } else if (changeWith.equals("*")) {
            return toChange * changeAmount;
        } else {
            return toChange;
        }
    }
    
    private static List<CustomMultiStack> change(String changeBy, List<CustomMultiStack> toChange) {
        if (changeBy == null) {
            return toChange;
        }
        final String changeWith = changeBy.substring(0, 1);
        final int changeAmount = Integer.parseInt(changeBy.substring(1));
        final List<CustomMultiStack> newStacks = new ArrayList<>();
        for (CustomMultiStack oldStack : toChange) {
            final long amount;
            if (changeWith.equals("/")) {
                amount = oldStack.amount() / changeAmount;
            } else if (changeWith.equals("*")) {
                amount = oldStack.amount() * changeAmount;
            } else {
                amount = oldStack.amount() / changeAmount;
            }
            newStacks.add(new CustomMultiStack(oldStack.stacks(), amount));
        }
        return newStacks;
    }
}
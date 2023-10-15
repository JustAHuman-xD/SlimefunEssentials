package me.justahuman.slimefun_essentials.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
    private static final JsonArray defaultAddons = new JsonArray();
    static {
        defaultAddons.add("Slimefun");
    }

    private static boolean blockFeatures = true;
    private static boolean recipeFeatures = true;
    private static boolean autoToggleAddons = true;
    private static List<String> addons = new ArrayList<>();
    
    public static void loadConfig() {
        JsonObject root = new JsonObject();
        try(final FileReader reader = new FileReader(getConfigFile())) {
            if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
                root = jsonObject;
            }
        } catch (Exception e) {
            Utils.warn("Error occurred while loading Config!");
            Utils.warn(e.getMessage());
        }
        
        try {
            blockFeatures = JsonUtils.getBooleanOrDefault(root, "block_features", true, true);
            recipeFeatures = JsonUtils.getBooleanOrDefault(root, "recipe_features", true, true);
            autoToggleAddons = JsonUtils.getBooleanOrDefault(root, "auto_toggle_addons", true, true);
            for (JsonElement addon : JsonUtils.getArrayOrDefault(root, "addons", defaultAddons, true)) {
                if (addon instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
                    addons.add(jsonPrimitive.getAsString());
                }
            }
        } catch (Exception e) {
            Utils.warn("Error occurred while loading Config!");
            Utils.warn(e.getMessage());
        }
    }
    
    public static void saveConfig() {
        MinecraftClient.getInstance().reloadResources();
        JsonObject root = new JsonObject();
        JsonArray addonArray = new JsonArray();
        for (String addon : addons) {
            addonArray.add(addon);
        }

        root.addProperty("block_features", blockFeatures);
        root.addProperty("recipe_features", recipeFeatures);
        root.addProperty("auto_toggle_addons", autoToggleAddons);
        root.add("addons", addonArray);
        
        try(final FileWriter fileWriter = new FileWriter(getConfigFile())) {
            gson.toJson(root, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            Utils.warn("Error occurred while saving Config!");
            Utils.warn(e.getMessage());
        }
    }
    
    public static boolean blockFeatures() {
        return blockFeatures;
    }
    public static void setBlockFeatures(boolean newValue) {
        blockFeatures = newValue;
    }

    public static boolean recipeFeatures() {
        return recipeFeatures;
    }

    public static void setRecipeFeatures(boolean newValue) {
        recipeFeatures = newValue;
    }

    public static boolean autoToggleAddons() {
        return autoToggleAddons;
    }
    public static void setAutoToggleAddons(boolean newValue) {
        autoToggleAddons = newValue;
    }
    
    public static List<String> getAddons() {
        return addons;
    }
    public static void setAddons(List<String> newValue) {
        addons = newValue;
    }
    
    public static File getConfigFile() {
        final File configFile = FabricLoader.getInstance().getConfigDir().resolve("slimefun_essentials.json").toFile();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    throw new IOException();
                }
                
            } catch(IOException | SecurityException e) {
                Utils.warn("Failed to create config file!");
                e.printStackTrace();
            }
        }
        return configFile;
    }
}
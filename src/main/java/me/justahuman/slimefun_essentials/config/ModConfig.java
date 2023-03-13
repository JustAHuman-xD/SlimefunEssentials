package me.justahuman.slimefun_essentials.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

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
    
    private static boolean itemGroupsEnabled = true;
    private static boolean useCustomTextures = true;
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
            itemGroupsEnabled = JsonUtils.getBooleanOrDefault(root, "item_groups_enabled", true, true);
            useCustomTextures = JsonUtils.getBooleanOrDefault(root, "use_custom_textures", true, true);
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
        
        root.addProperty("item_groups_enabled", itemGroupsEnabled);
        root.addProperty("use_custom_textures", useCustomTextures);
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
    
    public static Screen buildConfig(Screen parent) {
        if (!Utils.isClothConfigEnabled()) {
            return parent;
        }
        
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("slimefun_essentials.title"));
        
        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
    
        final ConfigCategory category = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.category"));
        
        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.option.item_groups"), itemGroupsEnabled)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.option.item_groups.tooltip"))
                .setSaveConsumer(newValue -> itemGroupsEnabled = newValue)
                .build());
    
        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.option.custom_textures"), useCustomTextures)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.option.custom_textures.tooltip"))
                .setSaveConsumer(newValue -> useCustomTextures = newValue)
                .build());
    
        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.option.auto_toggle_addons"), autoToggleAddons)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.option.auto_toggle_addons.tooltip"))
                .setSaveConsumer(newValue -> autoToggleAddons = newValue)
                .build());
        
        category.addEntry(entryBuilder.startStrList(Text.translatable("slimefun_essentials.option.addons"), addons)
                .setDefaultValue(new ArrayList<>(List.of("Slimefun")))
                .setTooltip(Text.translatable("slimefun_essentials.option.addons.tooltip"))
                .setSaveConsumer(newValue -> addons = newValue)
                .build());
        
        builder.setSavingRunnable(ModConfig::saveConfig);
        
        return builder.build();
    }
    
    public static boolean isItemGroupsEnabled() {
        return itemGroupsEnabled;
    }
    
    public static boolean shouldUseCustomTextures() {
        return useCustomTextures;
    }
    
    public static boolean shouldAutoToggleAddons() {
        return autoToggleAddons;
    }
    
    public static List<String> getAddons() {
        return addons;
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
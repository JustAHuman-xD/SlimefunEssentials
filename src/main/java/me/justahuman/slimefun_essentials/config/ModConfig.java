package me.justahuman.slimefun_essentials.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

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

    private static @Setter boolean blockFeatures = true;
    private static @Setter boolean recipeFeatures = true;
    private static @Setter @Getter List<String> addons = new ArrayList<>();

    private static @Setter boolean requireServerConnection = true;
    private static @Setter boolean autoToggleAddons = true;
    private static @Setter boolean enableServerWhitelist = false;
    private static @Setter @Getter List<String> enabledServers = new ArrayList<>();
    
    public static void loadConfig() {
        final JsonObject root = new JsonObject();
        try (final FileReader reader = new FileReader(getConfigFile())) {
            if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
                jsonObject.entrySet().forEach(entry -> root.add(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            Utils.warn("Error occurred while loading Config!");
            Utils.warn(e.getMessage());
        }

        loadConfigOption(() -> blockFeatures = JsonUtils.getBooleanOrDefault(root, "block_features", true, true));
        loadConfigOption(() -> recipeFeatures = JsonUtils.getBooleanOrDefault(root, "recipe_features", true, true));
        loadConfigOption(() -> {
            for (JsonElement addon : JsonUtils.getArrayOrDefault(root, "addons", defaultAddons, true)) {
                if (addon instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
                    addons.add(jsonPrimitive.getAsString());
                }
            }
        });

        loadConfigOption(() -> requireServerConnection = JsonUtils.getBooleanOrDefault(root, "require_server_connection", true, true));
        loadConfigOption(() -> autoToggleAddons = JsonUtils.getBooleanOrDefault(root, "auto_toggle_addons", true, true));
        loadConfigOption(() -> enableServerWhitelist = JsonUtils.getBooleanOrDefault(root, "enable_server_whitelist", false, true));
        loadConfigOption(() -> {
            for (JsonElement server : JsonUtils.getArrayOrDefault(root, "enabled_servers", new JsonArray(), true)) {
                if (server instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
                    enabledServers.add(jsonPrimitive.getAsString());
                }
            }
        });
    }

    private static void loadConfigOption(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            Utils.warn("Error occurred while loading Config!");
            Utils.warn(e.getMessage());
        }
    }
    
    public static void saveConfig() {
        MinecraftClient.getInstance().reloadResources();

        final JsonObject root = new JsonObject();
        final JsonArray addonArray = new JsonArray();
        for (String addon : addons) {
            addonArray.add(addon);
        }

        final JsonArray serverArray = new JsonArray();
        for (String server : enabledServers) {
            serverArray.add(server);
        }

        root.addProperty("block_features", blockFeatures);
        root.addProperty("recipe_features", recipeFeatures);
        root.add("addons", addonArray);

        root.addProperty("require_server_connection", requireServerConnection);
        root.addProperty("auto_toggle_addons", autoToggleAddons);
        root.addProperty("enable_server_whitelist", enableServerWhitelist);
        root.add("enabled_servers", serverArray);
        
        try (final FileWriter fileWriter = new FileWriter(getConfigFile())) {
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

    public static boolean recipeFeatures() {
        return recipeFeatures;
    }

    public static boolean requireServerConnection() {
        return requireServerConnection;
    }

    public static boolean autoToggleAddons() {
        return autoToggleAddons;
    }

    public static boolean enableServerWhitelist() {
        return enableServerWhitelist;
    }

    public static boolean isCurrentServerEnabled() {
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return false;
        }

        final ServerInfo serverInfo = client.getCurrentServerEntry();
        return serverInfo != null && enabledServers.contains(serverInfo.name);
    }

    public static boolean isServerEnabled(String server) {
        return enabledServers.contains(server);
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
                Utils.warn(e.getMessage());
            }
        }
        return configFile;
    }
}
package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    public static final String ID = "slimefun_essentials";
    private static final Logger LOGGER = LoggerFactory.getLogger(ID);
    private static final String ERROR_MESSAGE = "[SFE] Failed to load data";

    public static Identifier newIdentifier(String path) {
        return new Identifier(ID, path.toLowerCase());
    }

    public static boolean filterResources(Identifier identifier) {
        final String path = identifier.getPath();
        return path.endsWith(".json");
    }

    public static boolean filterAddons(Identifier identifier) {
        if (!filterResources(identifier)) {
            return false;
        }

        final String path = identifier.getPath();
        final String addon = getFileName(path);
        for (String enabledAddon : ModConfig.getAddons()) {
            if (enabledAddon.equalsIgnoreCase(addon)) {
                return true;
            }
        }

        return false;
    }

    public static boolean filterItems(Identifier identifier) {
        if (!filterResources(identifier)) {
            return false;
        }

        final String path = identifier.getPath();
        final String id = getFileName(path);
        return ResourceLoader.getSlimefunItems().containsKey(id.toUpperCase());
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));
    }

    public static String getSlimefunId(ItemStack itemStack) {
        final NbtCompound nbt = itemStack.getNbt();
        if (nbt == null || nbt.isEmpty() || !nbt.contains("PublicBukkitValues")) {
            return null;
        }

        final NbtCompound bukkitValues = nbt.getCompound("PublicBukkitValues");
        if (!bukkitValues.contains("slimefun:slimefun_item")) {
            return null;
        }

        return bukkitValues.getString("slimefun:slimefun_item");
    }

    public static boolean shouldFunction() {
        if (ModConfig.requireServerConnection() && !Utils.isOnMultiplayer()) {
            return false;
        }

        if (ModConfig.enableServerWhitelist() && !ModConfig.isCurrentServerEnabled()) {
            return false;
        }

        return true;
    }

    public static boolean isOnMultiplayer() {
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return false;
        }

        final ServerInfo server = client.getCurrentServerEntry();
        return server != null && !server.isLocal() && !server.isRealm();
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void warn(String warning) {
        LOGGER.warn(warning);
    }

    public static void error(Exception exception) {
        LOGGER.error(ERROR_MESSAGE);
        exception.printStackTrace();
    }
}

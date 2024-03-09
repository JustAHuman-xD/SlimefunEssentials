package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

    public static boolean equalSlimefunIds(ItemStack o1, ItemStack o2) {
        final NbtCompound n1 = o1.getNbt();
        final NbtCompound n2 = o2.getNbt();
        if (n1 == null || n1.isEmpty() || n2 == null || n2.isEmpty() || !n1.contains("PublicBukkitValues") || !n2.contains("PublicBukkitValues")) {
            return false;
        }

        final NbtCompound b1 = n1.getCompound("PublicBukkitValues");
        final NbtCompound b2 = n2.getCompound("PublicBukkitValues");
        if (!b1.contains("slimefun:slimefun_item") || !b2.contains("slimefun:slimefun_item")) {
            return false;
        }

        final String id1 = b1.getString("slimefun:slimefun_item");
        final String id2 = b2.getString("slimefun:slimefun_item");
        return id1 != null && id1.equals(id2);
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

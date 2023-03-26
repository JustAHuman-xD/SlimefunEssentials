package me.justahuman.slimefun_essentials.utils;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class Utils {
    public static final String ID = "slimefun_essentials";
    public static final Identifier ADDON_CHANNEL = new Identifier("recipe_exporter", "addon");
    public static final Identifier BLOCK_CHANNEL = new Identifier("recipe_exporter", "block");
    private static final Logger logger = LoggerFactory.getLogger(ID);
    private static final String errorMessage = "[SFE] Failed to load data";
    
    public static File restartFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("slimefun_essentials_restart.txt").toFile();
    }
    
    public static Identifier newIdentifier(String namespace) {
        return new Identifier(ID, namespace.toLowerCase());
    }
    
    public static boolean isClothConfigEnabled() {
        return FabricLoader.getInstance().isModLoaded("cloth-config2");
    }
    
    public static boolean isJeiEnabled() {
        return FabricLoader.getInstance().isModLoaded("jei");
    }
    
    public static boolean isReiEnabled() {
        return FabricLoader.getInstance().isModLoaded("rei");
    }
    
    public static void fillInputs(List<EmiIngredient> list, int size) {
        if (list.size() == size) {
            return;
        }
        
        for (int i = list.size(); i < size; i++) {
            list.add(EmiStack.EMPTY);
        }
    }
    
    public static void fillOutputs(List<EmiStack> list, int size) {
        if (list.size() == size) {
            return;
        }
        
        for (int i = list.size(); i < size; i++) {
            list.add(EmiStack.EMPTY);
        }
    }
    
    public static boolean equalSlimefunIds(ItemStack o1, ItemStack o2) {
        final NbtCompound n1 = o1.getNbt();
        final NbtCompound n2 = o2.getNbt();
        if (n1 == null || n1.isEmpty() || n2 == null || n2.isEmpty() || !n1.contains("PublicBukkitValues") || !n2.contains("PublicBukkitValues")) {
           return false;
        }
        
        final NbtCompound b1 = n1.getCompound("PublicBukkitValues");
        final NbtCompound b2 = n2.getCompound("PublicBukkitValues");
        if (!b1.contains("slimefun:slimefun_item") || b2.contains("slimefun:slimefun_item")) {
            return false;
        }
        
        final String id1 = b1.getString("slimefun:slimefun_item");
        final String id2 = b2.getString("slimefun:slimefun_item");
        return id1 != null && id1.equals(id2);
    }
    
    public static void log(String message) {
        logger.info(message);
    }
    
    public static void warn(String warning) {
        logger.warn(warning);
    }
    
    public static void error(Exception exception) {
        logger.error(errorMessage);
        exception.printStackTrace();
    }
}

package me.justahuman.slimefun_essentials.utils;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Utils {
    public static final String ID = "slimefun_essentials";
    public static final Identifier WIDGETS = new Identifier(ID, "textures/gui/widgets.png");
    public static final Identifier ADDON_CHANNEL = new Identifier("recipe_exporter", "addon");
    public static final Identifier BLOCK_CHANNEL = new Identifier("recipe_exporter", "block");
    private static final Logger logger = LoggerFactory.getLogger(ID);
    private static final String errorMessage = "[SFE] Failed to load data";
    
    public static Identifier newIdentifier(String namespace) {
        return new Identifier(ID, namespace.toLowerCase());
    }
    
    public static boolean isClothConfigEnabled() {
        return FabricLoader.getInstance().isModLoaded("cloth-config2");
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

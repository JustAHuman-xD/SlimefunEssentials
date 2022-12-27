package me.justahuman.slimefun_essentials.core;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger("slimefun_essentials");
    private static final String errorMessage = "[SFtoEMI] Failed to parse persistent data";
    public static final Identifier WIDGETS = new Identifier("slimefun_essentials", "textures/gui/widgets.png");

    public static void log(String message) {
        logger.info(message);
    }

    public static void error(Exception exception) {
        logger.error(errorMessage);
        exception.printStackTrace();
    }

    public static boolean isClothConfigEnabled() {
        return FabricLoader.getInstance().isModLoaded("cloth-config2");
    }
}

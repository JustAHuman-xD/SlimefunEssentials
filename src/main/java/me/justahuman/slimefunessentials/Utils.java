package me.justahuman.slimefunessentials;

import dev.emi.emi.api.render.EmiTexture;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    public static final int bigSlotHeight = 26;
    public static final int bigSlotWidth = 26;
    public static final int slotHeight = 18;
    public static final int slotWidth = 18;
    public static final int arrowHeight = 17;
    public static final int arrowWidth = 24;
    public static final int chargeWidth = 7;
    public static final int chargeHeight = 9;
    public static final Identifier WIDGETS = new Identifier("slimefunessentials", "textures/gui/widgets.png");
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(WIDGETS, 36, 0, chargeWidth, chargeHeight);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(WIDGETS, 43, 0, chargeWidth, chargeHeight);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(WIDGETS, 50, 0, chargeWidth, chargeHeight);
    private static final Logger logger = LoggerFactory.getLogger("slimefunessentials");
    private static final String errorMessage = "[SFtoEMI] Failed to parse persistent data";

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

package me.justahuman.slimefun_essentials.compatibility.emi;

import dev.emi.emi.api.render.EmiTexture;
import me.justahuman.slimefun_essentials.core.Utils;

import java.text.NumberFormat;

public class EmiUtils {
    public static final int padding = 4;
    public static final int bigSlotHeight = 26;
    public static final int bigSlotWidth = 26;
    public static final int slotHeight = 18;
    public static final int slotWidth = 18;
    public static final int arrowHeight = 17;
    public static final int arrowWidth = 24;
    public static final int chargeWidth = 7;
    public static final int chargeHeight = 9;
    public static final int conditionWidth = 13;
    public static final int conditionHeight = 13;
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(Utils.WIDGETS, 36, 0, chargeWidth, chargeHeight);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(Utils.WIDGETS, 43, 0, chargeWidth, chargeHeight);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(Utils.WIDGETS, 50, 0, chargeWidth, chargeHeight);
    public static final NumberFormat numberFormat = NumberFormat.getInstance();
    
    public static void load() {
        numberFormat.setGroupingUsed(true);
    }
}

package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiTexture;
import me.justahuman.slimefun_essentials.utils.Utils;

import java.text.NumberFormat;

public class EmiUtils {
    public static final int padding = 4;
    public static final int bigSlot = 26;
    public static final int slot = 18;
    public static final int arrowHeight = 17;
    public static final int arrowWidth = 24;
    public static final int chargeWidth = 7;
    public static final int chargeHeight = 9;
    public static final int label = 13;
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(Utils.WIDGETS, 36, 0, chargeWidth, chargeHeight);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(Utils.WIDGETS, 43, 0, chargeWidth, chargeHeight);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(Utils.WIDGETS, 50, 0, chargeWidth, chargeHeight);
    public static final EmiTexture BACKWARDS_EMPTY_ARROW = new EmiTexture(EmiRenderHelper.WIDGETS, 68, 0, 24, 17);
    public static final NumberFormat numberFormat = NumberFormat.getInstance();
    static {
        numberFormat.setGroupingUsed(true);
    }
}

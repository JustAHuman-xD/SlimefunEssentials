package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiTexture;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;

import java.text.NumberFormat;

public class EmiUtils {
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(Utils.WIDGETS, 36, 0, TextureUtils.chargeWidth, TextureUtils.chargeHeight);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(Utils.WIDGETS, 43, 0, TextureUtils.chargeWidth, TextureUtils.chargeHeight);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(Utils.WIDGETS, 50, 0, TextureUtils.chargeWidth, TextureUtils.chargeHeight);
    public static final EmiTexture BACKWARDS_EMPTY_ARROW = new EmiTexture(EmiRenderHelper.WIDGETS, 68, 0, 24, 17);
    public static final NumberFormat numberFormat = NumberFormat.getInstance();
    static {
        numberFormat.setGroupingUsed(true);
    }
}

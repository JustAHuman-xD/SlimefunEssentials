package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiTexture;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public class EmiUtils {
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(TextureUtils.WIDGETS, 36, 0, TextureUtils.energyWidth, TextureUtils.energyHeight);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(TextureUtils.WIDGETS, 43, 0, TextureUtils.energyWidth, TextureUtils.energyHeight);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(TextureUtils.WIDGETS, 50, 0, TextureUtils.energyWidth, TextureUtils.energyHeight);
    public static final EmiTexture BACKWARDS_EMPTY_ARROW = new EmiTexture(EmiRenderHelper.WIDGETS, 68, 0, 24, 17);
}

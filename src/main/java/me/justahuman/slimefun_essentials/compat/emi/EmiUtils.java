package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

import java.util.List;

public class EmiUtils {
    public static final EmiTexture EMPTY_CHARGE = new EmiTexture(TextureUtils.WIDGETS, 36, 0, TextureUtils.ENERGY_WIDTH, TextureUtils.ENERGY_HEIGHT);
    public static final EmiTexture GAIN_CHARGE = new EmiTexture(TextureUtils.WIDGETS, 43, 0, TextureUtils.ENERGY_WIDTH, TextureUtils.ENERGY_HEIGHT);
    public static final EmiTexture LOOSE_CHARGE = new EmiTexture(TextureUtils.WIDGETS, 50, 0, TextureUtils.ENERGY_WIDTH, TextureUtils.ENERGY_HEIGHT);
    public static final EmiTexture BACKWARDS_EMPTY_ARROW = new EmiTexture(EmiRenderHelper.WIDGETS, 68, 0, 24, 17);

    public static void fillInputs(List<EmiIngredient> list, int size) {
        if (list.size() >= size) {
            return;
        }

        for (int i = list.size(); i < size; i++) {
            list.add(EmiStack.EMPTY);
        }
    }

    public static void fillOutputs(List<EmiStack> list, int size) {
        if (list.size() >= size) {
            return;
        }

        for (int i = list.size(); i < size; i++) {
            list.add(EmiStack.EMPTY);
        }
    }
}

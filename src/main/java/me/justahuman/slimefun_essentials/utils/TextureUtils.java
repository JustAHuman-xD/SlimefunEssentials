package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureUtils {
    public static final int padding = 4;
    public static final int slotSize = 18;
    public static final int outputSize = 26;
    public static final int arrowHeight = 17;
    public static final int arrowWidth = 24;
    public static final int energyWidth = 7;
    public static final int energyHeight = 9;
    public static final int labelSize = 13;
    public static final Identifier WIDGETS = new Identifier(Utils.ID, "textures/gui/widgets.png");
    public static final Identifier WIDGETS_DARK = new Identifier(Utils.ID, "textures/gui/widgets_dark.png");
    public static final SlimefunLabel ENERGY = new SlimefunLabel("energy", WIDGETS, WIDGETS_DARK, 36, 0, energyWidth, energyHeight);
    public static final SlimefunLabel SLOT = new SlimefunLabel("slot", WIDGETS, WIDGETS_DARK, 0, 238, slotSize, slotSize);
    public static final SlimefunLabel OUTPUT = new SlimefunLabel("output", WIDGETS, WIDGETS_DARK, 18, 230, outputSize, outputSize);
    public static final SlimefunLabel ARROW = new SlimefunLabel("arrow", WIDGETS, WIDGETS_DARK, 44, 222, arrowWidth, arrowHeight);
    public static final SlimefunLabel BACKWARDS_ARROW = new SlimefunLabel("backwards_arrow", WIDGETS, WIDGETS_DARK, 67, 222, arrowWidth, arrowHeight);
    public static final Map<SlimefunCategory, Integer> CACHED_WIDTH = new HashMap<>();
    public static final Map<SlimefunCategory, Integer> CACHED_HEIGHT = new HashMap<>();
    public static final NumberFormat numberFormat = NumberFormat.getInstance();

    static {
        numberFormat.setGroupingUsed(true);
    }

    public static int getContentsWidth(SlimefunCategory slimefunCategory) {
        return CACHED_WIDTH.computeIfAbsent(slimefunCategory, value -> {
            int width = 0;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                width = Math.max(width, getContentsWidth(slimefunRecipe));
            }
            return width;
        });
    }

    public static int getContentsWidth(SlimefunRecipe slimefunRecipe) {
        return getContentsWidth(slimefunRecipe.labels(), slimefunRecipe.inputs(), slimefunRecipe.outputs(), slimefunRecipe.energy());
    }
    
    public static int getContentsWidth(List<?> labels, List<?> inputs, List<?> outputs, Integer energy) {
        return (labels != null ? (labelSize + padding) * labels.size() : 0) + (energy != null ? energyWidth + padding : 0) + ((slotSize + padding) * (inputs != null ? inputs.size(): 1)) + (arrowWidth + padding) + (outputs != null ? outputSize * outputs.size() + padding * (outputs.size() - 1) : 0);
    }

    public static int getContentsHeight(SlimefunCategory slimefunCategory) {
        return CACHED_HEIGHT.computeIfAbsent(slimefunCategory, value -> {
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                if (slimefunRecipe.outputs() != null && !slimefunRecipe.outputs().isEmpty()) {
                    return outputSize;
                }
            }

            return slotSize;
        });
    }

    public static int getContentsHeight(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.outputs() != null && !slimefunRecipe.outputs().isEmpty() ? outputSize : slotSize;
    }
}

package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;
import java.util.HashMap;
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
    public static final SlimefunLabel PEDESTAL = new SlimefunLabel("pedestal", WIDGETS, WIDGETS_DARK, 0, 0, slotSize, slotSize);
    public static final SlimefunLabel ALTAR = new SlimefunLabel("altar", WIDGETS, WIDGETS_DARK, 18, 0, slotSize, slotSize);
    public static final Map<SlimefunCategory, Integer> CACHED_WIDTH = new HashMap<>();
    public static final Map<SlimefunCategory, Integer> CACHED_HEIGHT = new HashMap<>();
    public static final NumberFormat numberFormat = NumberFormat.getInstance();

    static {
        numberFormat.setGroupingUsed(true);
    }

    public static int getSideSafe(String type) {
        try {
            return Integer.parseInt(type.substring(type.length() - 1));
        } catch (NumberFormatException ignored) {
            return 3;
        }
    }

    public static int getGridWidth(SlimefunCategory slimefunCategory, int side) {
        return CACHED_WIDTH.computeIfAbsent(slimefunCategory, value -> {
            int width = 0;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                width = Math.max(width, getGridWidth(slimefunRecipe, side));
            }
            return width;
        });
    }

    public static int getGridWidth(SlimefunRecipe slimefunRecipe, int side) {
        return (side * TextureUtils.slotSize + TextureUtils.padding) + (slimefunRecipe.hasEnergy() ? TextureUtils.energyWidth + TextureUtils.padding : 0) + (TextureUtils.arrowWidth + TextureUtils.padding) + (slimefunRecipe.hasOutputs()? TextureUtils.outputSize * slimefunRecipe.outputs().size() : 0);
    }

    public static int getGridHeight(int side) {
        return side * slotSize;
    }

    public static int getProcessWidth(SlimefunCategory slimefunCategory) {
        return CACHED_WIDTH.computeIfAbsent(slimefunCategory, value -> {
            int width = 0;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                width = Math.max(width, getProcessWidth(slimefunRecipe));
            }
            return width;
        });
    }
    
    public static int getProcessWidth(SlimefunRecipe slimefunRecipe) {
        return (slimefunRecipe.hasLabels() ? (labelSize + padding) * slimefunRecipe.labels().size() : 0) + (slimefunRecipe.hasEnergy() ? energyWidth + padding : 0) + ((slotSize + padding) * (slimefunRecipe.hasInputs() ? slimefunRecipe.inputs().size(): 1)) + (arrowWidth + padding) + (slimefunRecipe.hasOutputs() ? outputSize * slimefunRecipe.outputs().size() + padding * (slimefunRecipe.outputs().size() - 1) : 0);
    }

    public static int getProcessHeight(SlimefunCategory slimefunCategory) {
        return CACHED_HEIGHT.computeIfAbsent(slimefunCategory, value -> {
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                if (slimefunRecipe.hasOutputs()) {
                    return outputSize;
                }
            }

            return slotSize;
        });
    }

    public static int getProcessHeight(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.hasOutputs() ? outputSize : slotSize;
    }

    public static int getReactorWidth(SlimefunCategory slimefunCategory) {
        return CACHED_WIDTH.computeIfAbsent(slimefunCategory, value -> {
            int width = 0;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                width = Math.max(width, getReactorWidth(slimefunRecipe));
            }
            return width;
        });
    }

    public static int getReactorWidth(SlimefunRecipe slimefunRecipe) {
        return (TextureUtils.slotSize + TextureUtils.arrowWidth) * 2 + TextureUtils.padding * 4 + (slimefunRecipe.hasOutputs() ? TextureUtils.outputSize : TextureUtils.energyWidth);
    }

    public static int getReactorHeight(SlimefunCategory slimefunCategory) {
        return CACHED_HEIGHT.computeIfAbsent(slimefunCategory, value -> {
            final int baseAmount = TextureUtils.slotSize * 2;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                if (slimefunRecipe.hasOutputs()) {
                    return baseAmount + outputSize;
                }
            }

            return baseAmount + slotSize;
        });
    }

    public static int getReactorHeight(SlimefunRecipe slimefunRecipe) {
        return TextureUtils.slotSize * 2 + (slimefunRecipe.hasOutputs() ? TextureUtils.outputSize : TextureUtils.slotSize);
    }

    public static int getSmelteryWidth(SlimefunCategory slimefunCategory) {
        return CACHED_WIDTH.computeIfAbsent(slimefunCategory, value -> {
            int width = 0;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                width = Math.max(width, getSmelteryWidth(slimefunRecipe));
            }
            return width;
        });
    }

    public static int getSmelteryWidth(SlimefunRecipe slimefunRecipe) {
        return (slimefunRecipe.hasEnergy() ? TextureUtils.energyWidth + TextureUtils.padding : 0) + (slimefunRecipe.hasInputs() ? TextureUtils.slotSize * 2 + TextureUtils.padding : TextureUtils.slotSize + TextureUtils.padding) + (TextureUtils.arrowWidth + TextureUtils.padding) + (slimefunRecipe.hasOutputs() ? TextureUtils.outputSize * slimefunRecipe.outputs().size() + TextureUtils.padding * (slimefunRecipe.outputs().size() - 1): 0);
    }
}

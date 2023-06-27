package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class TextureUtils {
    public static final int PADDING = 4;
    public static final int REI_PADDING = 6;
    public static final int SLOT_SIZE = 18;
    public static final int OUTPUT_SIZE = 26;
    public static final int ARROW_HEIGHT = 17;
    public static final int ARROW_WIDTH = 24;
    public static final int ENERGY_WIDTH = 7;
    public static final int ENERGY_HEIGHT = 9;
    public static final int LABEL_SIZE = 13;
    public static final Identifier WIDGETS = new Identifier(Utils.ID, "textures/gui/widgets.png");
    public static final Identifier WIDGETS_DARK = new Identifier(Utils.ID, "textures/gui/widgets_dark.png");
    public static final SlimefunLabel ENERGY = new SlimefunLabel("energy", WIDGETS, WIDGETS_DARK, 36, 0, ENERGY_WIDTH, ENERGY_HEIGHT);
    public static final SlimefunLabel ENERGY_POSITIVE = new SlimefunLabel("energy_positive", WIDGETS, WIDGETS_DARK, 43, 0, ENERGY_WIDTH, ENERGY_HEIGHT);
    public static final SlimefunLabel ENERGY_NEGATIVE = new SlimefunLabel("energy_negative", WIDGETS, WIDGETS_DARK, 50, 0, ENERGY_WIDTH, ENERGY_HEIGHT);
    public static final SlimefunLabel SLOT = new SlimefunLabel("slot", WIDGETS, WIDGETS_DARK, 0, 238, SLOT_SIZE, SLOT_SIZE);
    public static final SlimefunLabel OUTPUT = new SlimefunLabel("output", WIDGETS, WIDGETS_DARK, 18, 230, OUTPUT_SIZE, OUTPUT_SIZE);
    public static final SlimefunLabel ARROW = new SlimefunLabel("arrow", WIDGETS, WIDGETS_DARK, 44, 222, ARROW_WIDTH, ARROW_HEIGHT);
    public static final SlimefunLabel FILLED_ARROW = new SlimefunLabel("filled_arrow", WIDGETS, WIDGETS_DARK, 44, 239, ARROW_WIDTH, ARROW_HEIGHT);
    public static final SlimefunLabel BACKWARDS_ARROW = new SlimefunLabel("backwards_arrow", WIDGETS, WIDGETS_DARK, 67, 222, ARROW_WIDTH, ARROW_HEIGHT);
    public static final SlimefunLabel FILLED_BACKWARDS_ARROW = new SlimefunLabel("filled_backwards_arrow", WIDGETS, WIDGETS_DARK, 67, 239, ARROW_WIDTH, ARROW_HEIGHT);
    public static final SlimefunLabel PEDESTAL = new SlimefunLabel("pedestal", WIDGETS, WIDGETS_DARK, 0, 0, SLOT_SIZE, SLOT_SIZE);
    public static final SlimefunLabel ALTAR = new SlimefunLabel("altar", WIDGETS, WIDGETS_DARK, 18, 0, SLOT_SIZE, SLOT_SIZE);
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
        return (side * TextureUtils.SLOT_SIZE + TextureUtils.PADDING) + (slimefunRecipe.hasEnergy() ? TextureUtils.ENERGY_WIDTH + TextureUtils.PADDING : 0) + (TextureUtils.ARROW_WIDTH + TextureUtils.PADDING) + (slimefunRecipe.hasOutputs()? TextureUtils.OUTPUT_SIZE * slimefunRecipe.outputs().size() : 0);
    }

    public static int getGridHeight(int side) {
        return side * SLOT_SIZE;
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
        return (slimefunRecipe.hasLabels() ? (LABEL_SIZE + PADDING) * slimefunRecipe.labels().size() : 0) + (slimefunRecipe.hasEnergy() ? ENERGY_WIDTH + PADDING : 0) + ((SLOT_SIZE + PADDING) * (slimefunRecipe.hasInputs() ? slimefunRecipe.inputs().size(): 1)) + (ARROW_WIDTH + PADDING) + (slimefunRecipe.hasOutputs() ? OUTPUT_SIZE * slimefunRecipe.outputs().size() + PADDING * (slimefunRecipe.outputs().size() - 1) : 0);
    }

    public static int getProcessHeight(SlimefunCategory slimefunCategory) {
        return CACHED_HEIGHT.computeIfAbsent(slimefunCategory, value -> {
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                if (slimefunRecipe.hasOutputs()) {
                    return OUTPUT_SIZE;
                }
            }

            return SLOT_SIZE;
        });
    }

    public static int getProcessHeight(SlimefunRecipe slimefunRecipe) {
        return slimefunRecipe.hasOutputs() ? OUTPUT_SIZE : SLOT_SIZE;
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
        return (TextureUtils.SLOT_SIZE + TextureUtils.ARROW_WIDTH) * 2 + TextureUtils.PADDING * 4 + (slimefunRecipe.hasOutputs() ? TextureUtils.OUTPUT_SIZE : TextureUtils.ENERGY_WIDTH);
    }

    public static int getReactorHeight(SlimefunCategory slimefunCategory) {
        return CACHED_HEIGHT.computeIfAbsent(slimefunCategory, value -> {
            final int baseAmount = TextureUtils.SLOT_SIZE * 2;
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                if (slimefunRecipe.hasOutputs()) {
                    return baseAmount + OUTPUT_SIZE;
                }
            }

            return baseAmount + SLOT_SIZE;
        });
    }

    public static int getReactorHeight(SlimefunRecipe slimefunRecipe) {
        return TextureUtils.SLOT_SIZE * 2 + (slimefunRecipe.hasOutputs() ? TextureUtils.OUTPUT_SIZE : TextureUtils.SLOT_SIZE);
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
        return (slimefunRecipe.hasEnergy() ? TextureUtils.ENERGY_WIDTH + TextureUtils.PADDING : 0) + (slimefunRecipe.hasInputs() ? TextureUtils.SLOT_SIZE * 2 + TextureUtils.PADDING : TextureUtils.SLOT_SIZE + TextureUtils.PADDING) + (TextureUtils.ARROW_WIDTH + TextureUtils.PADDING) + (slimefunRecipe.hasOutputs() ? TextureUtils.OUTPUT_SIZE * slimefunRecipe.outputs().size() + TextureUtils.PADDING * (slimefunRecipe.outputs().size() - 1): 0);
    }
}

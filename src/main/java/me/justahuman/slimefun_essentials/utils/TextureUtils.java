package me.justahuman.slimefun_essentials.utils;

import java.util.List;

public class TextureUtils {
    public static final int padding = 4;
    public static final int bigSlot = 26;
    public static final int slot = 18;
    public static final int arrowHeight = 17;
    public static final int arrowWidth = 24;
    public static final int chargeWidth = 7;
    public static final int chargeHeight = 9;
    public static final int label = 13;
    public static int getProcessWidth(List<?> labels, List<?> inputs, List<?> outputs, Integer energy) {
        return (labels != null ? label * labels.size() + padding * labels.size() : 0) + (inputs != null ? slot * inputs.size() + padding * inputs.size() : slot + padding) + (outputs != null ? bigSlot * outputs.size() + padding * (outputs.size() - 1) : 0) + (energy != null ? chargeWidth + padding : 0) + arrowWidth + padding;
    }
}

package me.justahuman.slimefun_essentials.api;

import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public class OffsetBuilder {
    protected final int labelOffset;
    protected final int energyOffset;
    protected final int slotOffset;
    protected final int arrowOffset;
    protected final int outputOffset;
    protected final int minY;
    protected Offset xOffset;
    protected Offset yOffset;

    public OffsetBuilder(RecipeRenderer recipeRenderer, SlimefunRecipe slimefunRecipe) {
        this(recipeRenderer, slimefunRecipe, recipeRenderer.calculateXOffset(slimefunRecipe), recipeRenderer.calculateYOffset(slimefunRecipe, 0));
    }

    public OffsetBuilder(RecipeRenderer recipeRenderer, SlimefunRecipe slimefunRecipe, int x) {
        this(recipeRenderer, slimefunRecipe, x, recipeRenderer.calculateYOffset(slimefunRecipe, 0));
    }

    public OffsetBuilder(RecipeRenderer recipeRenderer, SlimefunRecipe slimefunRecipe, int x, int y) {
        this(recipeRenderer, slimefunRecipe, x, y, 0);
    }

    public OffsetBuilder(RecipeRenderer recipeRenderer, SlimefunRecipe slimefunRecipe, int x, int y, int minY) {
        this.labelOffset = recipeRenderer.calculateYOffset(slimefunRecipe, TextureUtils.LABEL_SIZE) + minY;
        this.energyOffset = recipeRenderer.calculateYOffset(slimefunRecipe, TextureUtils.ENERGY_HEIGHT) + minY;
        this.slotOffset = recipeRenderer.calculateYOffset(slimefunRecipe, TextureUtils.SLOT_SIZE) + minY;
        this.arrowOffset = recipeRenderer.calculateYOffset(slimefunRecipe, TextureUtils.ARROW_HEIGHT) + minY;
        this.outputOffset = recipeRenderer.calculateYOffset(slimefunRecipe, TextureUtils.OUTPUT_SIZE) + minY;
        this.minY = minY;
        this.xOffset = new Offset(x);
        this.yOffset = new Offset(y);
    }

    public Offset x() {
        return xOffset;
    }

    public Offset setX(int x) {
        this.xOffset.set(x);
        return this.xOffset;
    }

    public int getX() {
        return this.xOffset.get();
    }

    public Offset y() {
        return yOffset;
    }

    public Offset setY(int y) {
        this.yOffset.set(y);
        return this.yOffset;
    }

    public int getY() {
        return this.yOffset.get();
    }

    public int minY() {
        return this.minY;
    }

    public int label() {
        return labelOffset;
    }

    public int energy() {
        return energyOffset;
    }

    public int slot() {
        return slotOffset;
    }

    public int arrow() {
        return arrowOffset;
    }

    public int output() {
        return outputOffset;
    }

    public static class Offset {
        int value;

        public Offset(int value) {
            this.value = value;
        }

        public int get() {
            return this.value;
        }

        public Offset set(int offset) {
            this.value = offset;
            return this;
        }

        public Offset add(int add) {
            this.value += add;
            return this;
        }

        public Offset subtract(int subtract) {
            this.value -= subtract;
            return this;
        }

        public void addLabel() {
            addLabel(true);
        }

        public void addLabel(boolean padding) {
            this.value += TextureUtils.LABEL_SIZE + (padding ? TextureUtils.PADDING : 0);
        }

        public void addEnergy() {
            addEnergy(true);
        }

        public void addEnergy(boolean padding) {
            this.value += TextureUtils.ENERGY_WIDTH + (padding ? TextureUtils.PADDING : 0);
        }

        public void addSlot() {
            addSlot(true);
        }

        public void addSlot(boolean padding) {
            this.value += TextureUtils.SLOT_SIZE + (padding ? TextureUtils.PADDING : 0);
        }

        public void addArrow() {
            addArrow(true);
        }

        public void addArrow(boolean padding) {
            this.value += TextureUtils.ARROW_WIDTH + (padding ? TextureUtils.PADDING : 0);
        }

        public void addOutput() {
            addOutput(true);
        }

        public void addOutput(boolean padding) {
            this.value += TextureUtils.OUTPUT_SIZE + (padding ? TextureUtils.PADDING : 0);
        }

        public Offset addPadding() {
            this.value += TextureUtils.PADDING;
            return this;
        }
    }
}

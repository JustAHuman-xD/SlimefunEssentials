package me.justahuman.slimefun_essentials.client.display;

import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.api.RecipeDisplay;
import me.justahuman.slimefun_essentials.client.RecipeComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.component.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.justahuman.slimefun_essentials.api.def.DefaultDisplayComponent.ARROW_RIGHT;
import static me.justahuman.slimefun_essentials.api.def.DefaultDisplayComponent.ENERGY;
import static me.justahuman.slimefun_essentials.api.def.DefaultDisplayComponent.FILLING_ARROW_RIGHT;
import static me.justahuman.slimefun_essentials.api.def.DefaultDisplayComponent.LARGE_SLOT;
import static me.justahuman.slimefun_essentials.api.def.DefaultDisplayComponent.SLOT;

public class DynamicDisplay implements RecipeDisplay {
    public static final DynamicDisplay INSTANCE = new DynamicDisplay();

    protected final Map<SlimefunRecipe, Snapshot> snapshots = new HashMap<>();

    private DynamicDisplay() {}

    @Override
    public String id() {
        return "dynamic";
    }

    @Override
    public int width(SlimefunRecipe recipe) {
        return snapshots.computeIfAbsent(recipe, Snapshot::of).width;
    }

    @Override
    public int height(SlimefunRecipe recipe) {
        return snapshots.computeIfAbsent(recipe, Snapshot::of).height;
    }

    @Override
    public List<RecipeDisplayComponent> components(SlimefunRecipe recipe) {
        return snapshots.computeIfAbsent(recipe, Snapshot::of).components;
    }

    public static void clear() {
        INSTANCE.snapshots.clear();
    }

    public record Snapshot(List<RecipeDisplayComponent> components, int width, int height) {
        static Snapshot of(SlimefunRecipe recipe) {
            List<RecipeDisplayComponent> components = new ArrayList<>();
            int width = TextureUtils.PADDING * 2;
            int height = TextureUtils.PADDING * 2;

            if (recipe.hasLabels()) {
                for (String type : recipe.labels()) {
                    DisplayComponentType component = DisplayComponentType.get(type);
                    width += component.width() + TextureUtils.PADDING;
                    height = Math.max(height, component.height() + TextureUtils.PADDING * 2);
                }
            }

            if (recipe.hasEnergy()) {
                DisplayComponentType type = DisplayComponentType.get(ENERGY);
                width += type.width() + TextureUtils.PADDING;
                height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
            }

            int inputHeight = 0;
            if (recipe.hasInputs()) {
                int inputWidth = 0;
                int rows = (int) Math.ceil((double) recipe.inputs().size() / 5);
                for (int i = 0; i < rows; i++) {
                    int rowHeight = 0;
                    int rowWidth = 0;
                    for (int j = 0; j < 5; j++) {
                        int index = i * 5 + j;
                        if (index >= recipe.inputs().size()) break;
                        DisplayComponentType type = DisplayComponentType.get(recipe.inputs().get(index).isLarge() ? LARGE_SLOT : SLOT);
                        rowWidth += type.width();
                        rowHeight = Math.max(rowHeight, type.height());
                    }
                    inputWidth = Math.max(inputWidth, rowWidth);
                    inputHeight += rowHeight;
                }

                width += inputWidth + TextureUtils.PADDING;
                height = Math.max(height, inputHeight + TextureUtils.PADDING * 2);
            } else {
                DisplayComponentType type = DisplayComponentType.get(SLOT);
                width += type.width();
                height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
            }

            if (recipe.hasEnergy() || recipe.hasOutputs()) {
                width += TextureUtils.PADDING;
                DisplayComponentType type = DisplayComponentType.get(ARROW_RIGHT);
                width += type.width() + TextureUtils.PADDING;
                height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
            }

            int outputHeight = 0;
            if (recipe.hasOutputs()) {
                int outputWidth = 0;
                int rows = (int) Math.ceil((double) recipe.outputs().size() / 5);
                for (int i = 0; i < rows; i++) {
                    int rowHeight = 0;
                    int rowWidth = 0;
                    for (int j = 0; j < 5; j++) {
                        int index = i * 5 + j;
                        if (index >= recipe.outputs().size()) break;
                        DisplayComponentType type = DisplayComponentType.get(recipe.outputs().get(index).isLarge() ? LARGE_SLOT : SLOT);
                        rowWidth += type.width();
                        rowHeight = Math.max(rowHeight, type.height());
                    }
                    outputWidth = Math.max(outputWidth, rowWidth);
                    outputHeight += rowHeight;
                }

                width += outputWidth + TextureUtils.PADDING;
                height = Math.max(height, outputHeight + TextureUtils.PADDING * 2);
            }

            int x = TextureUtils.PADDING;

            if (recipe.hasLabels()) {
                for (String type : recipe.labels()) {
                    DisplayComponentType component = DisplayComponentType.get(type);
                    components.add(new RecipeDisplayComponent(component.type(), x, centered(component, height)));
                    x += component.width() + TextureUtils.PADDING;
                }
            }

            if (recipe.hasEnergy() && recipe.hasOutputs()) {
                DisplayComponentType type = DisplayComponentType.get(ENERGY);
                components.add(new RecipeDisplayComponent(type.type(), x, centered(type, height)));
                x += type.width() + TextureUtils.PADDING;
            }

            if (recipe.hasInputs()) {
                int startX = x;
                int rowHeight = 0;
                int y = (height - inputHeight) / 2;
                int i = 1;
                for (RecipeComponent component : recipe.inputs()) {
                    if ((i - 1) % 5 == 0) {
                        x = startX;
                        y += rowHeight;
                        rowHeight = 0;
                    }

                    DisplayComponentType type = DisplayComponentType.get(component.isLarge() ? LARGE_SLOT : SLOT);
                    components.add(new RecipeDisplayComponent(type.type(), x, y, i));
                    x += type.width();
                    rowHeight = Math.max(rowHeight, type.height());
                    i++;
                }
            } else {
                DisplayComponentType type = DisplayComponentType.get(SLOT);
                components.add(new RecipeDisplayComponent(SLOT, x, centered(type, height)));
                x += type.width();
            }

            if (recipe.hasEnergy() || recipe.hasOutputs()) {
                x += TextureUtils.PADDING;
                DisplayComponentType type = DisplayComponentType.get(FILLING_ARROW_RIGHT);
                components.add(new RecipeDisplayComponent(FILLING_ARROW_RIGHT, x, centered(type, height)));
                x += type.width() + TextureUtils.PADDING;
            }

            if (recipe.hasOutputs()) {
                int startX = x;
                int rowHeight = 0;
                int y = (height - outputHeight) / 2;
                int i = 1;
                for (RecipeComponent component : recipe.outputs()) {
                    if ((i - 1) % 5 == 0) {
                        x = startX;
                        y += rowHeight;
                        rowHeight = 0;
                    }

                    DisplayComponentType type = DisplayComponentType.get(component.isLarge() ? LARGE_SLOT : SLOT);
                    components.add(new RecipeDisplayComponent(type.type(), x, y, i, true));
                    x += type.width();
                    rowHeight = Math.max(rowHeight, type.height());
                    i++;
                }
            } else if (recipe.hasEnergy()) {
                DisplayComponentType type = DisplayComponentType.get(ENERGY);
                components.add(new RecipeDisplayComponent(type.type(), x, centered(type, height)));
            }

            return new Snapshot(components, width, height);
        }

        private static int centered(DisplayComponentType type, int height) {
            return (height - type.height()) / 2;
        }

        private static int centered(RecipeDisplayComponent component, int height) {
            return (height - component.height()) / 2;
        }
    }
}

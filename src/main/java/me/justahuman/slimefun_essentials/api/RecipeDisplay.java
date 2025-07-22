package me.justahuman.slimefun_essentials.api;

import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.component.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.display.BasicDisplay;
import me.justahuman.slimefun_essentials.client.display.DynamicDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RecipeDisplay {
    RecipeDisplay NONE = new BasicDisplay("none", 0, 0, new ArrayList<>());
    Map<String, RecipeDisplay> DEFAULT_DISPLAYS = Map.of(
            "basic", new BasicDisplay("basic", 0, 0, new ArrayList<>()),
            "dynamic", DynamicDisplay.INSTANCE
    );
    Map<String, RecipeDisplay> RECIPE_DISPLAYS = new HashMap<>(DEFAULT_DISPLAYS);

    String id();
    int width(SlimefunRecipe recipe);
    int height(SlimefunRecipe recipe);
    List<RecipeDisplayComponent> components(SlimefunRecipe recipe);

    static RecipeDisplay get(String id) {
        return RECIPE_DISPLAYS.getOrDefault(id, NONE);
    }

    static void clear() {
        RECIPE_DISPLAYS.clear();
        RECIPE_DISPLAYS.putAll(DEFAULT_DISPLAYS);
        DynamicDisplay.clear();
    }
}

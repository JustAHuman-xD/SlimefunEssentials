package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SlimefunCategory {
    private static final Map<String, SlimefunCategory> slimefunCategories = new LinkedHashMap<>();
    
    @Getter
    private final String id;
    @Getter
    private final String type;
    @Getter
    private final Integer speed;
    @Getter
    private final Integer energy;
    @Getter
    private final List<SlimefunRecipe> recipes;
    
    public SlimefunCategory(String id, String type, Integer speed, Integer energy, List<SlimefunRecipe> recipes) {
        this.id = id;
        this.type = type;
        this.speed = speed;
        this.energy = energy;
        this.recipes = recipes;
        
        slimefunCategories.put(id, this);
    }
    
    public static void deserialize(String id, JsonObject categoryObject) {
        final String type = JsonUtils.getStringOrDefault(categoryObject, "type", "process");
        final Integer speed = JsonUtils.getIntegerOrDefault(categoryObject, "speed", null);
        final Integer energy = JsonUtils.getIntegerOrDefault(categoryObject, "energy", null);
        final List<SlimefunRecipe> recipes = new ArrayList<>();
        for (JsonElement recipeElement : JsonUtils.getArrayOrDefault(categoryObject, "recipes", new JsonArray())) {
            if (!(recipeElement instanceof JsonObject recipeObject)) {
                continue;
            }
        
            recipes.add(SlimefunRecipe.deserialize(recipeObject));
        }
        
        final String copy = JsonUtils.getStringOrDefault(categoryObject, "copy", "");
        final List<SlimefunRecipe> copiedRecipes = slimefunCategories.containsKey(copy) ? slimefunCategories.get(copy).getRecipes() : new ArrayList<>();
        recipes.addAll(copiedRecipes);
        
        new SlimefunCategory(id, type, speed, energy, recipes);
    }
    
    /**
     * Returns an unmodifiable version of {@link SlimefunCategory#slimefunCategories}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, SlimefunCategory> getSlimefunCategories() {
        return Collections.unmodifiableMap(slimefunCategories);
    }
}

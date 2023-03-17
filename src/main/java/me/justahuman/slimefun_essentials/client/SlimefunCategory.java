package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record SlimefunCategory(String id, String type, Integer speed, Integer energy, List<SlimefunRecipe> recipes) {
    private static final Map<String, SlimefunCategory> slimefunCategories = new LinkedHashMap<>();
    
    public static void deserialize(String id, JsonObject categoryObject) {
        final String type = JsonUtils.getStringOrDefault(categoryObject, "type", "process");
        final Integer speed = JsonUtils.getIntegerOrDefault(categoryObject, "speed", null);
        final Integer energy = JsonUtils.getIntegerOrDefault(categoryObject, "energy", null);
        final List<SlimefunRecipe> recipes = new ArrayList<>();
        for (JsonElement recipeElement : JsonUtils.getArrayOrDefault(categoryObject, "recipes", new JsonArray())) {
            if (! (recipeElement instanceof JsonObject recipeObject)) {
                continue;
            }
    
            recipes.add(SlimefunRecipe.deserialize(recipeObject, energy));
        }
        
        final String copy = JsonUtils.getStringOrDefault(categoryObject, "copy", "");
        final List<SlimefunRecipe> copiedRecipes = slimefunCategories.containsKey(copy) ? slimefunCategories.get(copy).recipes() : new ArrayList<>();
        recipes.addAll(copiedRecipes);
    
        slimefunCategories.put(id, new SlimefunCategory(id, type, speed, energy, recipes));
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

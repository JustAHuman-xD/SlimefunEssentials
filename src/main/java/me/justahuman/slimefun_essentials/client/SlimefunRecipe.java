package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public record SlimefunRecipe(Integer time, Integer energy, List<SlimefunRecipeComponent> inputs, List<SlimefunRecipeComponent> outputs, List<SlimefunLabel> labels) {
    public static SlimefunRecipe deserialize(JsonObject recipeObject, Integer workstationEnergy) {
        final Integer time = JsonUtils.getIntegerOrDefault(recipeObject, "time", null);
        final Integer energy = JsonUtils.getIntegerOrDefault(recipeObject, "energy", workstationEnergy);
        final List<SlimefunRecipeComponent> inputs = new ArrayList<>();
        final List<SlimefunRecipeComponent> outputs = new ArrayList<>();
        final List<SlimefunLabel> labels = new ArrayList<>();
        
        for (JsonElement inputElement : JsonUtils.getArrayOrDefault(recipeObject, "inputs", new JsonArray())) {
            final SlimefunRecipeComponent inputRecipeElement = SlimefunRecipeComponent.deserialize(inputElement);
            if (inputRecipeElement != null) {
                inputs.add(inputRecipeElement);
            }
        }
        
        for (JsonElement outputElement : JsonUtils.getArrayOrDefault(recipeObject, "outputs", new JsonArray())) {
            final SlimefunRecipeComponent outputRecipeElement = SlimefunRecipeComponent.deserialize(outputElement);
            if (outputRecipeElement != null) {
                outputs.add(outputRecipeElement);
            }
        }
        
        for (JsonElement labelElement : JsonUtils.getArrayOrDefault(recipeObject, "labels", new JsonArray())) {
            if (! (labelElement instanceof JsonPrimitive jsonPrimitive) || ! jsonPrimitive.isString()) {
                continue;
            }
            
            final SlimefunLabel slimefunLabel = SlimefunLabel.getSlimefunLabels().get(jsonPrimitive.getAsString());
            if (slimefunLabel != null) {
                labels.add(slimefunLabel);
            }
        }
        
        return new SlimefunRecipe(time, energy, inputs, outputs, labels);
    }
}

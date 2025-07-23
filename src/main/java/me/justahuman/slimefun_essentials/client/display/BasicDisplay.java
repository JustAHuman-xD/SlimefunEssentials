package me.justahuman.slimefun_essentials.client.display;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.api.RecipeDisplay;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.component.RecipeDisplayComponent;

import java.util.ArrayList;
import java.util.List;

public record BasicDisplay(
        String id,
        int width, int height,
        List<RecipeDisplayComponent> components
) implements RecipeDisplay {
    public int width(SlimefunRecipe recipe) {
        return width;
    }

    public int height(SlimefunRecipe recipe) {
        return height;
    }

    public List<RecipeDisplayComponent> components(SlimefunRecipe recipe) {
        return components;
    }

    public static void deserialize(ByteArrayDataInput input, int dataVersion) {
        String id = input.readUTF();
        try {
            int width = input.readInt();
            int height = input.readInt();
            List<RecipeDisplayComponent> components = new ArrayList<>();

            int componentsSize = input.readInt();
            for (int i = 0; i < componentsSize; i++) {
                components.add(RecipeDisplayComponent.deserialize(input));
            }

            RECIPE_DISPLAYS.put(id, new BasicDisplay(id, width, height, components));
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Failed to deserialize BasicDisplay: {}", id, e);
        }
    }

    public static void deserialize(String id, JsonObject displayObject) {
        JsonArray serializedComponents = displayObject.getAsJsonArray("components");
        List<RecipeDisplayComponent> components = new ArrayList<>();
        for (int i = 0; i < serializedComponents.size(); i++) {
            JsonObject componentObject = serializedComponents.get(i).getAsJsonObject();
            components.add(RecipeDisplayComponent.deserialize(componentObject));
        }

        int width = displayObject.get("width").getAsInt();
        int height = displayObject.get("height").getAsInt();
        RECIPE_DISPLAYS.put(id, new BasicDisplay(id, width, height, components));
    }
}

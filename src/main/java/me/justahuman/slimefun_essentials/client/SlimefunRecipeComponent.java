package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SlimefunRecipeComponent {
    @Getter
    private final String id;
    @Getter
    private final List<String> multiId;
    
    public SlimefunRecipeComponent(String id) {
        this.id = id;
        this.multiId = null;
    }
    
    public SlimefunRecipeComponent(List<String> multiId) {
        this.id = null;
        this.multiId = multiId;
    }
    
    public static SlimefunRecipeComponent deserialize(JsonElement componentElement) {
        if (componentElement instanceof JsonPrimitive componentPrimitive && componentPrimitive.isString()) {
            return new SlimefunRecipeComponent(componentPrimitive.getAsString());
        } else if (componentElement instanceof JsonArray componentArray) {
            final List<String> multiId = new ArrayList<>();
            for (JsonElement idElement : componentArray) {
                if (idElement instanceof JsonPrimitive idPrimitive && idPrimitive.isString()) {
                    multiId.add(idPrimitive.getAsString());
                }
            }
            return new SlimefunRecipeComponent(multiId);
        }
        return null;
    }

    public List<String> getMultiId() {
        return this.multiId;
    }

    public String getId() {
        return this.id;
    }
}

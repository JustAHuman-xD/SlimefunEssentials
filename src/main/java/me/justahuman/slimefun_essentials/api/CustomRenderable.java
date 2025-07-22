package me.justahuman.slimefun_essentials.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.client.renderable.ConditionalRenderable;
import me.justahuman.slimefun_essentials.client.renderable.OptionalRenderable;
import me.justahuman.slimefun_essentials.client.renderable.SimpleRenderable;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public interface CustomRenderable {
    Identifier identifier();
    int width();
    int height();
    int u();
    int v();
    int textureWidth();
    int textureHeight();
    List<TooltipComponent> tooltip();

    default void update(SlimefunRecipe recipe) {}
    default boolean canRender() {
        return true;
    }

    static CustomRenderable deserialize(ByteArrayDataInput input) {
        if (input.readBoolean()) {
            if (input.readBoolean()) {
                return new ConditionalRenderable(
                        deserialize(input),
                        deserialize(input),
                        RecipeCondition.deserialize(input)
                );
            }
            return new OptionalRenderable(
                    deserialize(input),
                    RecipeCondition.deserialize(input)
            );
        } else {
            return SimpleRenderable.deserialize(input);
        }
    }

    static CustomRenderable deserialize(JsonObject jsonObject) {
        if (jsonObject.has("condition")) {
            if (jsonObject.has("passed") && jsonObject.has("failed")) {
                return new ConditionalRenderable(
                        deserialize(jsonObject.getAsJsonObject("passed")),
                        deserialize(jsonObject.getAsJsonObject("failed")),
                        RecipeCondition.deserialize(jsonObject.get("condition"))
                );
            }
            return new OptionalRenderable(
                    deserialize(jsonObject.getAsJsonObject("renderable")),
                    RecipeCondition.deserialize(jsonObject.get("condition"))
            );
        } else {
            return SimpleRenderable.deserialize(jsonObject);
        }
    }
}

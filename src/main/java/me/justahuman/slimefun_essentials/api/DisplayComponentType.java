package me.justahuman.slimefun_essentials.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.api.def.DrawMode;
import me.justahuman.slimefun_essentials.client.component.BasicComponentType;
import me.justahuman.slimefun_essentials.client.component.FillingComponentType;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.mixins.minecraft.DrawContextInvoker;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DisplayComponentType {
    Map<String, DisplayComponentType> COMPONENT_TYPES = new HashMap<>();

    String type();
    int width();
    int height();
    List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe);

    void draw(SlimefunRecipe recipe, DrawMode mode, DrawContext context, int x, int y);

    default void draw(SlimefunRecipe recipe, CustomRenderable renderable, DrawContext context, int x, int y) {
        renderable.update(recipe);
        if (renderable.canRender()) {
            draw(recipe, context, renderable.identifier(), x, y, renderable.width(), renderable.height(), renderable.u(), renderable.v(), renderable.width(), renderable.height(), renderable.textureWidth(), renderable.textureHeight());
        }
    }

    default void draw(SlimefunRecipe recipe, DrawContext context, Identifier identifier, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawTexture(RenderLayer::getGuiTextured, identifier, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    default void drawTooltip(DrawContext context, List<TooltipComponent> tooltip,  int x, int y, int mouseX, int mouseY, int width, int height) {
        if (!tooltip.isEmpty() && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            ((DrawContextInvoker) context).callDrawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, x, y, HoveredTooltipPositioner.INSTANCE, null);
        }
    }

    static void clear() {
        COMPONENT_TYPES.clear();
    }

    static DisplayComponentType get(String id) {
        return COMPONENT_TYPES.get(id);
    }

    static void deserialize(ByteArrayDataInput input) {
        String id = input.readUTF();
        if (COMPONENT_TYPES.containsKey(id)) {
            // TODO: ??
            return;
        }

        try {
            CustomRenderable light = CustomRenderable.deserialize(input);
            CustomRenderable dark = CustomRenderable.deserialize(input);
            if (input.readBoolean()) {
                CustomRenderable lightFill = CustomRenderable.deserialize(input);
                CustomRenderable darkFill = CustomRenderable.deserialize(input);
                String timeToFill = input.readUTF();
                boolean horizontal = input.readBoolean();
                RecipeCondition emptyToFull = RecipeCondition.deserialize(input);
                RecipeCondition startToEnd = RecipeCondition.deserialize(input);

                COMPONENT_TYPES.put(id, new FillingComponentType(
                        id,
                        light, lightFill,
                        dark, darkFill,
                        recipe -> Utils.resolveNumberPlaceholder(timeToFill, recipe, 1000).intValue(),
                        horizontal,
                        emptyToFull, startToEnd
                ));
            } else {
                COMPONENT_TYPES.put(id, new BasicComponentType(id, light, dark));
            }
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Failed to deserialize DisplayComponentType: {}", id, e);
        }
    }

    static void deserialize(String id, JsonObject type) {
        if (COMPONENT_TYPES.containsKey(id)) {
            // TODO: ??
            return;
        }

        CustomRenderable light = CustomRenderable.deserialize(type.getAsJsonObject("light"));
        CustomRenderable dark = CustomRenderable.deserialize(type.getAsJsonObject("dark"));
        if (type.has("light_fill")) {
            CustomRenderable lightFill = CustomRenderable.deserialize(type.getAsJsonObject("light_fill"));
            CustomRenderable darkFill = CustomRenderable.deserialize(type.getAsJsonObject("dark_fill"));
            String timeToFill = type.get("time_to_fill").getAsString();
            boolean horizontal = type.get("horizontal").getAsBoolean();
            RecipeCondition emptyToFull = RecipeCondition.deserialize(type.get("empty_to_full"));
            RecipeCondition startToEnd = RecipeCondition.deserialize(type.get("start_to_end"));
            COMPONENT_TYPES.put(id, new FillingComponentType(
                    id,
                    light, lightFill,
                    dark, darkFill,
                    recipe -> Utils.resolveNumberPlaceholder(timeToFill, recipe, 1000).intValue(),
                    horizontal,
                    emptyToFull, startToEnd
            ));
        } else {
            COMPONENT_TYPES.put(id, new BasicComponentType(id, light, dark));
        }
    }
}

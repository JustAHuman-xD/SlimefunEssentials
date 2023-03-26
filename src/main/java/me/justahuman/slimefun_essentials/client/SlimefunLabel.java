package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record SlimefunLabel(String id, Identifier light, Identifier dark, int u, int v, int width, int height) {
    private static final Map<String, SlimefunLabel> slimefunLabels = new LinkedHashMap<>();

    public SlimefunLabel(String id, Identifier light, Identifier dark, int u, int v) {
        this(id, light, dark, u, v, TextureUtils.labelSize, TextureUtils.labelSize);
    }

    public static void deserialize(String id, JsonObject labelObject) {
        slimefunLabels.put(id, new SlimefunLabel(
                id,
                new Identifier(JsonUtils.getStringOrDefault(labelObject, "light", "slimefun_essentials:textures/gui/widgets.png")),
                new Identifier(JsonUtils.getStringOrDefault(labelObject, "dark", "slimefun_essentials:textures/gui/widgets_dark.png")),
                JsonUtils.getIntegerOrDefault(labelObject, "u", 0),
                JsonUtils.getIntegerOrDefault(labelObject, "v", 0)
        ));
    }
    
    /**
     * Returns an unmodifiable version of {@link SlimefunLabel#slimefunLabels}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, SlimefunLabel> getSlimefunLabels() {
        return Collections.unmodifiableMap(slimefunLabels);
    }

    public void draw(MatrixStack stack, int x, int y, boolean dark) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, dark ? this.dark : this.light);
        DrawableHelper.drawTexture(stack, x, y, width, height, this.u, this.v, width, height, 256, 256);
    }

    public void draw(MatrixStack stack, int x, int y) {
        draw(stack, x, y, false);
    }
}

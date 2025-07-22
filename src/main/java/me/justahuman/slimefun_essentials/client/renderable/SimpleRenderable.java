package me.justahuman.slimefun_essentials.client.renderable;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.utils.DataUtils;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public record SimpleRenderable(
        Identifier identifier,
        int width, int height,
        int u, int v,
        int textureWidth, int textureHeight,
        List<TooltipComponent> tooltip
) implements CustomRenderable {
    public static SimpleRenderable deserialize(ByteArrayDataInput input) {
        return new SimpleRenderable(
            Identifier.tryParse(DataUtils.get(input, TextureUtils.WIDGETS.toString())),
            input.readInt(),
            input.readInt(),
            DataUtils.get(input, 0),
            DataUtils.get(input, 0),
            DataUtils.get(input, 256),
            DataUtils.get(input, 256),
            DataUtils.getTooltip(input)
        );
    }

    public static SimpleRenderable deserialize(JsonObject json) {
        return new SimpleRenderable(
            Identifier.tryParse(JsonUtils.get(json, "identifier", TextureUtils.WIDGETS.toString())),
            json.get("width").getAsInt(),
            json.get("height").getAsInt(),
            JsonUtils.get(json, "u", 0),
            JsonUtils.get(json, "v", 0),
            JsonUtils.get(json, "texture_width", 256),
            JsonUtils.get(json, "texture_height", 256),
            JsonUtils.getTooltip(json)
        );
    }
}

package me.justahuman.slimefun_essentials.client.component;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.api.def.DrawMode;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.DataUtils;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RecipeDisplayComponent(
        String type,
        int x, int y,
        int index,
        boolean output,
        CustomRenderable renderable,
        List<TooltipComponent> tooltipOverride,
        Map<SlimefunRecipe, List<TooltipComponent>> tooltipCache
) implements DisplayComponentType {
    public static final List<TooltipComponent> EMPTY_TOOLTIP = List.of();

    public RecipeDisplayComponent(String type, int x, int y) {
        this(type, x, y, -1, false, null, EMPTY_TOOLTIP, new HashMap<>());
    }

    public RecipeDisplayComponent(String type, int x, int y, int index) {
        this(type, x, y, index, false, null, EMPTY_TOOLTIP, new HashMap<>());
    }

    public RecipeDisplayComponent(String type, int x, int y, int index, boolean output) {
        this(type, x, y, index, output, null, EMPTY_TOOLTIP, new HashMap<>());
    }

    @Override
    public int width() {
        return this.renderable != null ? this.renderable.width() : getType().width();
    }

    @Override
    public int height() {
        return this.renderable != null ? this.renderable.height() : getType().height();
    }

    @Override
    public void draw(SlimefunRecipe recipe, DrawMode mode, DrawContext context, int x, int y) {
        if (this.renderable != null) {
            getType().draw(recipe, this.renderable, context, x, y);
        } else {
            getType().draw(recipe, mode, context, x, y);
        }
    }

    @Override
    public List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe) {
        return tooltipCache.computeIfAbsent(recipe, key -> {
            if (this.tooltipOverride.isEmpty()) {
                return getType().tooltip(drawMode, recipe);
            }
            return Utils.updateTooltip(this.tooltipOverride, recipe);
        });
    }

    public DisplayComponentType getType() {
        return DisplayComponentType.get(this.type);
    }

    public static RecipeDisplayComponent deserialize(ByteArrayDataInput input) {
        String type = input.readUTF();
        int x = input.readInt();
        int y = input.readInt();
        int index = DataUtils.get(input, -1);
        boolean output = input.readBoolean();
        CustomRenderable renderable = null;
        if (input.readBoolean()) {
            renderable = CustomRenderable.deserialize(input);
        }
        List<TooltipComponent> tooltipOverride = DataUtils.getTooltip(input);
        return new RecipeDisplayComponent(type, x, y, index, output, renderable, tooltipOverride, new HashMap<>());
    }

    public static RecipeDisplayComponent deserialize(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();
        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();
        int index = JsonUtils.get(jsonObject, "index", -1);
        boolean output = JsonUtils.get(jsonObject, "output", false);
        CustomRenderable renderable = null;
        if (jsonObject.has("renderable")) {
            renderable = CustomRenderable.deserialize(jsonObject.getAsJsonObject("renderable"));
        }
        List<TooltipComponent> tooltip = JsonUtils.getTooltip(jsonObject);
        return new RecipeDisplayComponent(type, x, y, index, output, renderable, tooltip, new HashMap<>());
    }
}

package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SlimefunLabel {
    private static final Map<String, SlimefunLabel> slimefunLabels = new LinkedHashMap<>();
    
    @Getter
    private final String id;
    @Getter
    private final Identifier identifier;
    @Getter
    private final int u;
    @Getter
    private final int v;
    
    public SlimefunLabel(String id, Identifier identifier, int u, int v) {
        this.id = id;
        this.identifier = identifier;
        this.u = u;
        this.v = v;
    }
    
    public static void deserialize(String id, JsonObject labelObject) {
        slimefunLabels.put(id, new SlimefunLabel(
                id,
                new Identifier(JsonUtils.getStringOrDefault(labelObject, "identifier", "slimefun_essentials:textures/gui/widgets.png")),
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
}

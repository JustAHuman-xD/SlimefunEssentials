package me.justahuman.slimefun_essentials.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class RecipeComponent {
    public static final RecipeComponent EMPTY = new RecipeComponent(new ArrayList<>(), "");
    private final List<ItemStack> complex;
    private final String id;
    private final List<String> multiId;
    
    public RecipeComponent(List<ItemStack> complex, String id) {
        this(complex, id, null);
    }
    
    public RecipeComponent(List<ItemStack> complex, List<String> multiId) {
        this(complex, null, multiId);
    }

    private RecipeComponent(List<ItemStack> complex, String id, List<String> multiId) {
        this.id = id;
        this.multiId = multiId;
        this.complex = complex;
    }

    public boolean isLarge() {
        if (this.id != null) {
            return id.startsWith("@") && !id.startsWith("baby_", 1);
        } else if (this.multiId != null) {
            for (String id : multiId) {
                if (id.startsWith("@") && !id.startsWith("baby_", 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static RecipeComponent deserialize(List<ItemStack> complex, ByteArrayDataInput input) {
        final String[] elements = input.readUTF().split(",");
        if (elements.length == 1) {
            return new RecipeComponent(complex, elements[0]);
        }
        return new RecipeComponent(complex, Arrays.asList(elements));
    }

    public static RecipeComponent deserialize(List<ItemStack> complex, JsonElement element) {
        if (element instanceof JsonPrimitive primitive && primitive.isString()) {
            final String[] elements = primitive.getAsString().split(",");
            if (elements.length == 1) {
                return new RecipeComponent(complex, elements[0]);
            }
            return new RecipeComponent(complex, Arrays.asList(elements));
        }
        return null;
    }
}

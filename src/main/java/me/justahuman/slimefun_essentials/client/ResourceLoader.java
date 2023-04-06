package me.justahuman.slimefun_essentials.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResourceLoader {
    private static final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, SlimefunItemStack> slimefunItems = new LinkedHashMap<>();
    private static final Map<String, Identifier> slimefunBlocks = new HashMap<>();
    private static final Map<BlockPos, String> placedBlocks = new HashMap<>();

    /**
     * Clears all loaded Slimefun Items, SlimefunBlocks, and Categories
     */
    public static void clear() {
        slimefunItems.clear();
        slimefunBlocks.clear();
    }
    
    /**
     * Takes an {@link InputStream} from a given {@link Resource} and gets a {@link JsonObject} from it
     *
     * @param resource The {@link Resource} that contains the {@link JsonObject}
     *
     * @return The {@link JsonObject} from the {@link Resource}
     */
    public static JsonObject jsonObjectFromResource(Resource resource) {
        try {
            final InputStream inputStream = resource.getInputStream();
            return gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        } catch(IOException e) {
            Utils.error(e);
            return new JsonObject();
        }
    }
    
    /**
     * Load the Items from a given {@link Resource}
     *
     * @param resource The {@link Resource} that contains Slimefun Items
     */
    public static void loadItems(Resource resource) {
        final JsonObject items = jsonObjectFromResource(resource);
        for (String id : items.keySet()) {
            final JsonElement itemElement = items.get(id);
            if (!(itemElement instanceof JsonObject itemObject) || !itemObject.has("item") || !itemObject.has("nbt")) {
                continue;
            }
            
            slimefunItems.put(id, new SlimefunItemStack(id, JsonUtils.deserializeItem(itemObject)));
        }
        
        sortItems();
    }
    
    /**
     * Load the {@link SlimefunCategory} from a given {@link Resource}
     *
     * @param resource The {@link Resource} that contains an {@link SlimefunCategory} for a Slimefun Item
     */
    public static void loadCategories(Resource resource) {
        final JsonObject slimefunCategories = jsonObjectFromResource(resource);
        for (String id : slimefunCategories.keySet()) {
            final JsonObject categoryObject = slimefunCategories.getAsJsonObject(id);
            SlimefunCategory.deserialize(id, categoryObject);
        }
    }

    /**
     * Load the {@link SlimefunLabel} from a given {@link Resource}
     *
     * @param resource The {@link Resource} that contains a {@link SlimefunLabel}
     */
    public static void loadLabels(Resource resource) {
        final JsonObject slimefunLabels = jsonObjectFromResource(resource);
        for (String id : slimefunLabels.keySet()) {
            final JsonObject labelObject = slimefunLabels.getAsJsonObject(id);
            SlimefunLabel.deserialize(id, labelObject);
        }
    }

    /**
     * Adds a {@link Identifier} for a placed {@link Block} to change it's model when it's a slimefun block
     *
     * @param id The {@link String} id that represents a Slimefun Item
     * @param model The {@link Identifier} of the model that should be applied for a Slimefun Item
     */
    public static void addSlimefunBlock(String id, Identifier model) {
        slimefunBlocks.put(id, model);
    }

    /**
     * Adds a {@link BlockPos} for a placed Slimefun Item, with the {@link String} id of what Slimefun Item it is
     *
     * @param blockPos The {@link BlockPos} representing the location of a placed Slimefun Item
     * @param id The {@link String} id that represents a Slimefun Item
     */
    public static void addPlacedBlock(BlockPos blockPos, String id) {
        placedBlocks.put(blockPos, id);
    }

    /**
     * Removes a {@link BlockPos} for a placed Slimefun Item
     *
     * @param blockPos The {@link BlockPos} representing the location of the previously placed Slimefun Item
     */
    public static void removePlacedBlock(BlockPos blockPos) {
        placedBlocks.remove(blockPos);
    }

    /**
     * Returns an unmodifiable Map of all Slimefun ItemStacks, {@link String} -> Slimefun ID, {@link ItemStack} -> Slimefun ItemStack
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, SlimefunItemStack> getSlimefunItems() {
        return Collections.unmodifiableMap(slimefunItems);
    }

    @NonNull
    public static Map<String, Identifier> getSlimefunBlocks() {
        return Collections.unmodifiableMap(slimefunBlocks);
    }

    @NonNull
    public static Map<BlockPos, String> getPlacedBlocks() {
        return Collections.unmodifiableMap(placedBlocks);
    }
    
    private static void sortItems() {
        final Map<String, SlimefunItemStack> sortedSlimefunItems = new LinkedHashMap<>();
        final List<String> ids = new ArrayList<>(slimefunItems.keySet());
        ids.sort(Comparator.naturalOrder());
        
        for (String id : ids) {
            sortedSlimefunItems.put(id, slimefunItems.get(id));
        }
        
        slimefunItems.clear();
        slimefunItems.putAll(sortedSlimefunItems);
        sortedSlimefunItems.clear();
        ids.clear();
    }
}

package me.justahuman.slimefun_essentials.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceLoader {
    private static final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, SlimefunItemStack> slimefunItems = new LinkedHashMap<>();
    private static final Set<String> itemBlacklist = new HashSet<>();

    private static final Map<String, Identifier> blockModels = new HashMap<>();
    private static final Map<String, Long> itemModels = new HashMap<>();

    private static final Map<ChunkPos, Set<BlockPos>> placedChunks = new HashMap<>();
    private static final Map<BlockPos, String> placedBlocks = new HashMap<>();

    /**
     * Clears {@link ResourceLoader#slimefunItems}, {@link ResourceLoader#itemBlacklist}, {@link ResourceLoader#blockModels}, {@link ResourceLoader#itemModels}, {@link SlimefunLabel#clear()}, {@link SlimefunCategory#clear()}
     */
    public static void clear() {
        slimefunItems.clear();
        itemBlacklist.clear();
        blockModels.clear();
        itemModels.clear();
        SlimefunLabel.clear();
        SlimefunCategory.clear();
    }

    /**
     * Clears {@link ResourceLoader#placedBlocks} & {@link ResourceLoader#placedChunks}
     */
    public static void clearPlacedBlocks() {
        placedBlocks.clear();
        placedChunks.clear();
    }

    /**
     * Locates and loads all the data {@link SlimefunEssentials} requires to function
     *
     * @param manager The {@link ResourceManager} to load from
     */
    public static void loadResources(ResourceManager manager) {
        if (CompatUtils.isRecipeModLoaded()) {
            loadItems(manager);
            loadLabels(manager);
            loadCategories(manager);
        }

        if (ModConfig.blockFeatures()) {
            if (CompatUtils.isMoreBlockPredicatesLoaded()) {
                loadItems(manager);
                loadBlockModels(manager);
            }

            if (CompatUtils.isJadeLoaded()) {
                loadItems(manager);
            }
        }
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
     * Locates and loads every Item from the "slimefun/items" directory
     *
     * @param manager The {@link ResourceManager} to load from
     */
    public static void loadItems(ResourceManager manager) {
        for (Resource resource : manager.findResources("slimefun/items", Utils::filterAddons).values()) {
            ResourceLoader.loadItems(resource);
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

    public static SlimefunItemStack getSlimefunItem(String id) {
        return slimefunItems.get(id);
    }

    /**
     * Locates and loads every Label from the "slimefun/labels" directory
     *
     * @param manager The {@link ResourceManager} to load from
     */
    public static void loadLabels(ResourceManager manager) {
        for (Resource resource : manager.findResources("slimefun/labels", Utils::filterAddons).values()) {
            ResourceLoader.loadLabels(resource);
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
     * Locates and loads every Category from the "slimefun/categories" directory
     *
     * @param manager The {@link ResourceManager} to load from
     */
    public static void loadCategories(ResourceManager manager) {
        for (Resource resource : manager.findResources("slimefun/categories", Utils::filterAddons).values()) {
            ResourceLoader.loadCategories(resource);
        }
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
     * Locates and loads every Slimefun Block Model from the "models/block" directory
     *
     * @param manager The {@link ResourceManager} to load from
     */
    public static void loadBlockModels(ResourceManager manager) {
        for (Identifier identifier : manager.findResources("models/block", Utils::filterItems).keySet()) {
            final String id = Utils.getFileName(identifier.getPath());
            Utils.log("Loaded Block Model For: " + id);
            ResourceLoader.addBlockModel(id);
        }
    }

    /**
     * Adds a {@link String} id of an available {@link Block}'s Model
     *
     * @param id The {@link String} id that represents a Slimefun Item
     */
    public static void addBlockModel(String id) {
        blockModels.put(id, new Identifier("minecraft", "block/" + id));
    }

    /**
     * Adds a {@link BlockPos} for a placed Slimefun Item, with the {@link String} id of what Slimefun Item it is
     *
     * @param blockPos The {@link BlockPos} representing the location of a placed Slimefun Item
     * @param id The {@link String} id that represents a Slimefun Item
     */
    public static void addPlacedBlock(BlockPos blockPos, String id) {
        final ChunkPos chunkPos = new ChunkPos(blockPos);
        final Set<BlockPos> blocks = placedChunks.getOrDefault(chunkPos, new HashSet<>());

        blocks.add(blockPos);
        placedBlocks.put(blockPos, id);
        placedChunks.put(chunkPos, blocks);
    }

    /**
     * Removes all the cached {@link BlockPos} for a given {@link ChunkPos}
     *
     * @param chunkPos The {@link ChunkPos} to remove
     */
    public static void removePlacedChunk(ChunkPos chunkPos) {
        placedChunks.getOrDefault(chunkPos, new HashSet<>()).forEach(placedBlocks::remove);
        placedChunks.remove(chunkPos);
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
     * Adds a {@link String} id to the Item Blacklist
     *
     * @param id The {@link String} id representing a Slimefun Item
     */
    public static void blacklistItem(String id) {
        itemBlacklist.add(id);
    }

    /**
     * Checks if a {@link String} id is in the Item Blacklist
     *
     * @param id The {@link String} id representing a Slimefun Item
     *
     * @return If the {@link String} id is in the Item Blacklist
     */
    public static boolean isItemBlacklisted(String id) {
        return itemBlacklist.contains(id);
    }

    /**
     * Clears {@link ResourceLoader#itemBlacklist}
     */
    public static void clearItemBlacklist() {
        itemBlacklist.clear();
    }

    /**
     * Adds an Item Model for a given {@link String} id
     *
     * @param id The {@link String} id that represents a Slimefun Item
     * @param model The {@link Long} model for the Slimefun Item
     */
    public static void addItemModel(String id, long model) {
        if (slimefunItems.containsKey(id)) {
            itemModels.put(id, model);
        }
    }

    /**
     * Checks if a {@link String} id has a registered Item Model
     *
     * @param id The {@link String} id to check
     *
     * @return If the {@link String} id has a registered Item Model
     */
    public static boolean hasItemModel(String id) {
        return itemModels.containsKey(id);
    }

    /**
     * Gets the Item Model for a given {@link String} id
     *
     * @param id The {@link String} id to get the Item Model for
     *
     * @return The Item Model for the given {@link String} id
     */
    public static long getItemModel(String id) {
        return itemModels.getOrDefault(id, 0L);
    }

    /**
     * Clears {@link ResourceLoader#itemModels}
     */
    public static void clearItemModels() {
        itemModels.clear();
    }

    /**
     * Checks if a {@link BlockPos} is a placed Slimefun Item
     *
     * @param blockPos The {@link BlockPos} to check
     *
     * @return If the {@link BlockPos} is a Slimefun Item
     */
    public static boolean isSlimefunItem(BlockPos blockPos) {
        return placedBlocks.containsKey(blockPos);
    }

    public static String getPlacedId(BlockPos blockPos) {
        return placedBlocks.get(blockPos);
    }

    /**
     * Returns an unmodifiable version of {@link ResourceLoader#slimefunItems}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, SlimefunItemStack> getSlimefunItems() {
        return Collections.unmodifiableMap(slimefunItems);
    }

    /**
     * Returns an unmodifiable version of {@link ResourceLoader#blockModels}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, Identifier> getBlockModels() {
        return Collections.unmodifiableMap(blockModels);
    }

    /**
     * Returns an unmodifiable version of {@link ResourceLoader#placedBlocks}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<BlockPos, String> getPlacedBlocks() {
        return Collections.unmodifiableMap(placedBlocks);
    }

    /**
     * Sorts {@link ResourceLoader#slimefunItems} based on id's
     */
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

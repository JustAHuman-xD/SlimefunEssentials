package me.justahuman.slimefun_essentials.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.resource.Resource;

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
import java.util.Set;

public class ResourceLoader {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, ItemStack> SLIMEFUN_ITEMS = new LinkedHashMap<>();
    
    /**
     * Clears all loaded Slimefun Items & Recipes
     */
    public static void clear() {
        SLIMEFUN_ITEMS.clear();
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
            InputStream inputStream = resource.getInputStream();
            return GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        } catch(IOException e) {
            Utils.error(e);
            return new JsonObject();
        }
    }
    
    /**
     * Load the given Items from an {@link Resource}
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
            
            SLIMEFUN_ITEMS.put(id, Utils.deserializeItem(itemObject));
        }
        
        sortItems();
    }
    
    /**
     * Load the given Recipes from an {@link Resource}
     *
     * @param resource The {@link Resource} that contains slimefun recipes
     */
    public static void loadRecipes(Resource resource) {
        // TODO handle recipes
    }
    
    /**
     * Load the {@link ItemGroup}s from item_groups.json
     *
     * @param resource The {@link Resource} that contains the {@link ItemGroup}s
     */
    public static void loadItemGroups(Resource resource) {
        ItemGroups.reset();
        
        final JsonObject itemGroups = jsonObjectFromResource(resource);
        for (String id : itemGroups.keySet()) {
            final JsonObject groupObject = itemGroups.getAsJsonObject(id);
            final JsonElement iconElement = groupObject.get("icon");
            if (!(iconElement instanceof JsonPrimitive iconPrimitive) || !iconPrimitive.isString() || !SLIMEFUN_ITEMS.containsKey(iconPrimitive.getAsString())) {
                continue;
            }
            
            final ItemStack icon = SLIMEFUN_ITEMS.get(iconPrimitive.getAsString());
            final Set<ItemStack> entries = ItemStackSet.create();
            for (JsonElement entryElement : groupObject.getAsJsonArray("stacks")) {
                if (!(entryElement instanceof JsonPrimitive entryPrimitive) || !entryPrimitive.isString() || !SLIMEFUN_ITEMS.containsKey(entryPrimitive.getAsString())) {
                    continue;
                }
                
                entries.add(SLIMEFUN_ITEMS.get(entryPrimitive.getAsString()));
            }
            
            ItemGroups.addItemGroup(id, icon, entries);
        }
    }
    
    /**
     * Returns an unmodifiable Map of all Slimefun ItemStacks, String -> Slimefun ID, ItemStack -> Slimefun ItemStack
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, ItemStack> getSlimefunItems() {
        return Collections.unmodifiableMap(SLIMEFUN_ITEMS);
    }
    
    private static void sortItems() {
        final Map<String, ItemStack> sortedSlimefunItems = new HashMap<>();
        final List<String> ids = new ArrayList<>(SLIMEFUN_ITEMS.keySet());
        ids.sort(Comparator.naturalOrder());
        
        for (String id : ids) {
            sortedSlimefunItems.put(id, SLIMEFUN_ITEMS.get(id));
        }
        
        SLIMEFUN_ITEMS.clear();
        SLIMEFUN_ITEMS.putAll(sortedSlimefunItems);
        sortedSlimefunItems.clear();
        ids.clear();
    }
}

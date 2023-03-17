package me.justahuman.slimefun_essentials.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.mixins.ItemGroupAccessor;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceLoader {
    private static final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, SlimefunItemStack> slimefunItems = new LinkedHashMap<>();
    private static final Map<Identifier, ItemGroup> itemGroups = new HashMap<>();
    
    /**
     * Clears all loaded Slimefun Items, ItemGroups, and Categories
     */
    public static void clear() {
        slimefunItems.clear();
        
        for (Map.Entry<Identifier, ItemGroup> itemGroupEntry : itemGroups.entrySet()) {
            // TODO all of this stuff
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
     * Load the {@link ItemGroup}s from a given {@link Resource}
     *
     * @param resource The {@link Resource} that contains the {@link ItemGroup}s
     */
    public static void loadItemGroups(Resource resource) {
        final JsonObject itemGroups = jsonObjectFromResource(resource);
        for (String id : itemGroups.keySet()) {
            final JsonObject groupObject = itemGroups.getAsJsonObject(id);
            final JsonElement iconElement = groupObject.get("icon");
            if (!(iconElement instanceof JsonObject iconObject)|| !iconObject.has("item") || !iconObject.has("nbt")) {
                continue;
            }
            
            final ItemStack icon = JsonUtils.deserializeItem(iconObject);
            final Collection<ItemStack> displayStacks = ItemStackSet.create();
            final Set<ItemStack> searchTabStacks = ItemStackSet.create();
            for (JsonElement entryElement : groupObject.getAsJsonArray("stacks")) {
                if (!(entryElement instanceof JsonPrimitive entryPrimitive) || !entryPrimitive.isString() || ! slimefunItems.containsKey(entryPrimitive.getAsString())) {
                    continue;
                }
                
                displayStacks.add(slimefunItems.get(entryPrimitive.getAsString()).itemStack());
            }
            searchTabStacks.addAll(displayStacks);
            
            addItemGroup(id, icon, displayStacks, searchTabStacks);
        }
    }
    
    public static void addItemGroup(String id, ItemStack icon, Collection<ItemStack> displayStacks, Set<ItemStack> searchTabStacks) {
        final Identifier identifier = Utils.newIdentifier(id);
        if (itemGroups.get(identifier) instanceof ItemGroupAccessor itemGroupAccessor) {
            // TODO all of this stuff
            return;
        }
        
        itemGroups.put(identifier, FabricItemGroup.builder(Utils.newIdentifier(id)).icon(() -> icon).entries(((enabledFeatures, groupEntries, operatorEnabled) -> groupEntries.addAll(displayStacks))).build());
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
    
    public static void loadLabels(Resource resource) {
        final JsonObject slimefunLabels = jsonObjectFromResource(resource);
        for (String id : slimefunLabels.keySet()) {
            final JsonObject labelObject = slimefunLabels.getAsJsonObject(id);
            SlimefunLabel.deserialize(id, labelObject);
        }
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
    
    /**
     * Returns an unmodifiable Map of all Slimefun ItemGroups, {@link Identifier} -> The Identifier for the {@link ItemGroup}, {@link ItemGroup} -> The {@link ItemGroup} corresponding to the {@link Identifier}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<Identifier, ItemGroup> getItemGroups() {
        return Collections.unmodifiableMap(itemGroups);
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

package me.justahuman.slimefun_essentials.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, ItemStack> SLIMEFUN_ITEMS = new HashMap<>();
    private static final ItemStack SLIMEFUN_HEAD = new ItemStack(Items.PLAYER_HEAD);
    private static final String SLIMEFUN_HEAD_NBT = "{SkullOwner:{Id:[I;586957903,-571390605,-2106958415,202465300],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVjZmRjYjgyZGFlZGJiMjRhYTczN2Q2MzhiN2VmOWNkNGFmYjhjNGFiNzcwMDMyMzE0OTY3NjY3NGM2ODkyZCJ9fX0=\"}]}},display:{Name:'{\"text\":\"\"}'}}";
    static {
        SLIMEFUN_HEAD.setNbt(Utils.parseNbt(SLIMEFUN_HEAD_NBT));
    }
    
    /**
     * Clears all loaded Slimefun Items & Recipes
     */
    public static void clear() {
        SLIMEFUN_ITEMS.clear();
    }
    
    /**
     * Load the given Items from an {@link Resource}
     *
     * @param resource The {@link Resource} that contains Slimefun Items
     */
    public static void loadItems(Resource resource) {
        final InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        } catch(IOException e) {
            Utils.error(e);
            return;
        }
        
        final JsonObject items = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        for (String id : items.keySet()) {
            SLIMEFUN_ITEMS.put(id, Utils.deserializeItem(items.getAsJsonObject(id)));
        }
    }
    
    /**
     * Load the given Recipes from an {@link Resource}
     *
     * @param resource the resource that contains slimefun recipes
     */
    public static void loadRecipes(Resource resource) {
        // TODO handle recipes
    }
    
    /**
     * Returns the Player Head with the Slimefun Logo, this is most likely temporary
     * @return {@link ResourceLoader#SLIMEFUN_HEAD}
     */
    @NonNull
    public static ItemStack getSlimefunHead() {
        return SLIMEFUN_HEAD;
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
}

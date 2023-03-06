package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SlimefunEssentials implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (Utils.isClothConfigEnabled()) {
            AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        }
    
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Utils.ID, "reload_listener");
            }
    
            @Override
            public void reload(ResourceManager manager) {
                ResourceLoader.clear();
                
                // Load all the Items
                for (Resource resource : manager.findResources("slimefun/items", path -> path.getPath().endsWith(".json")).values()) {
                    ResourceLoader.loadItems(resource);
                }
                
                // Load all the Recipes
                for (Resource resource : manager.findResources("slimefun/recipes", path -> path.getPath().endsWith(".json")).values()) {
                    ResourceLoader.loadRecipes(resource);
                }
                
                // Load all the Item Groups
                for (Resource resource : manager.findResources("slimefun", path -> path.getPath().endsWith("item_groups.json")).values()) {
                    ResourceLoader.loadItemGroups(resource);
                }
            }
        });
    }
}
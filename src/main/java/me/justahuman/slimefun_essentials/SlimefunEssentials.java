package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimefunEssentials implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (Utils.isClothConfigEnabled()) {
            AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        }
    
        FabricLoader.getInstance().getModContainer(Utils.ID)
                .map(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        Utils.newIdentifier("one_item_group"),
                        container,
                        Text.literal("SFE: OneItemGroup"),
                        ResourcePackActivationType.NORMAL
                )).filter(success -> !success).ifPresent(success -> Utils.warn("Could not register built-in resource pack with custom name."));
    
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Utils.newIdentifier("reload_listener");
            }
    
            @Override
            public void reload(ResourceManager manager) {
                ResourceLoader.clear();
                
                // Load all the Items
                for (Resource resource : manager.findResources("slimefun/items", path -> path.getPath().endsWith(".json")).values()) {
                    ResourceLoader.loadItems(resource);
                }
                
                // Load all the Labels
                for (Resource resource : manager.findResources("slimefun/labels", path -> path.getPath().endsWith(".json")).values()) {
                    ResourceLoader.loadLabels(resource);
                }
                
                // Load all the Recipes
                for (Resource resource : manager.findResources("slimefun/item_groups", path -> path.getPath().endsWith(".json")).values()) {
                    ResourceLoader.loadItemGroups(resource);
                }
                
                // Load all the Item Groups
                for (Resource resource : manager.findResources("slimefun/categories", path -> path.getPath().endsWith(".json")).values()) {
                    ResourceLoader.loadCategories(resource);
                }
            }
        });
    }
}
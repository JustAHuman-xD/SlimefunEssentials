package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
        ModConfig.loadConfig();
        
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
                for (Resource resource : manager.findResources("slimefun/items", SlimefunEssentials::filterResources).values()) {
                    ResourceLoader.loadItems(resource);
                }
                
                // Load all the Labels
                for (Resource resource : manager.findResources("slimefun/labels", SlimefunEssentials::filterResources).values()) {
                    ResourceLoader.loadLabels(resource);
                }
                
                // Load all the Recipes
                for (Resource resource : manager.findResources("slimefun/item_groups", SlimefunEssentials::filterResources).values()) {
                    ResourceLoader.loadItemGroups(resource);
                }
                
                // Load all the Item Groups
                for (Resource resource : manager.findResources("slimefun/categories", SlimefunEssentials::filterResources).values()) {
                    ResourceLoader.loadCategories(resource);
                }
            }
        });
        
        if (ModConfig.shouldAutoToggleAddons()) {
            ClientPlayNetworking.registerGlobalReceiver(Utils.ADDON_CHANNEL, ((client, handler, buf, sender) -> {
            
            }));
        }
        
        if (ModConfig.shouldUseCustomTextures()) {
            ClientPlayNetworking.registerGlobalReceiver(Utils.BLOCK_CHANNEL, ((client, handler, buf, sender) -> {
        
            }));
        }
    }
    
    public static boolean filterResources(Identifier identifier) {
        final String path = identifier.getPath();
        if (!path.endsWith(".json")) {
            return false;
        }
    
        final String addon = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));
        for (String enabledAddon : ModConfig.getAddons()) {
            if (enabledAddon.equalsIgnoreCase(addon)) {
                return true;
            }
        }
        
        return false;
    }
}
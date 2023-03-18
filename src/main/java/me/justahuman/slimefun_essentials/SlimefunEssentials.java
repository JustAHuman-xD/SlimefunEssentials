package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SlimefunEssentials implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModConfig.loadConfig();
        shouldRestart();
        
        if (Utils.isClothConfigEnabled()) {
            final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("slimefun_essentials.open_config", GLFW.GLFW_KEY_F1, "slimefun_essentials.title"));
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (keyBinding.isPressed()) {
                    client.setScreen(ConfigScreen.buildScreen(client.currentScreen));
                }
            });
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
        
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if (shouldRestart()) {
                MinecraftClient.getInstance().scheduleStop();
            }
        }));
    }
    
    public static boolean shouldRestart() {
        if (Utils.restartFile().exists()) {
            try {
                boolean successful = Utils.restartFile().delete();
                if (!successful) {
                    throw new RuntimeException();
                }
                return true;
            } catch (RuntimeException ignored) {
                Utils.warn("Could not remove restart file!");
                return false;
            }
        }
        return false;
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
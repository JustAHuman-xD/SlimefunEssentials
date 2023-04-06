package me.justahuman.slimefun_essentials;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SlimefunEssentials implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.loadConfig();
        shouldRestart();
        
        if (Utils.isClothConfigEnabled()) {
            final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("slimefun_essentials.open_config", GLFW.GLFW_KEY_F6, "slimefun_essentials.title"));
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (keyBinding.isPressed()) {
                    client.setScreen(ConfigScreen.buildScreen(client.currentScreen));
                }
            });
        }
    
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Utils.newIdentifier("reload_listener");
            }
    
            @Override
            public void reload(ResourceManager manager) {
                ResourceLoader.clear();
                
                // Load all the Items
                for (Resource resource : manager.findResources("slimefun/items", SlimefunEssentials::filterAddons).values()) {
                    ResourceLoader.loadItems(resource);
                }
                
                // Load all the Labels
                for (Resource resource : manager.findResources("slimefun/labels", SlimefunEssentials::filterAddons).values()) {
                    ResourceLoader.loadLabels(resource);
                }
                
                // Load all the Recipes
                for (Resource resource : manager.findResources("slimefun/categories", SlimefunEssentials::filterAddons).values()) {
                    ResourceLoader.loadCategories(resource);
                }

                // Load all the Blocks
                for (Identifier identifier : manager.findResources("models", SlimefunEssentials::filterItems).keySet()) {
                    String id = getFileName(identifier.getPath());
                    ResourceLoader.addSlimefunBlock(id, identifier);
                }
            }
        });
        
        if (ModConfig.shouldAutoToggleAddons()) {
            final List<String> normalAddons = new ArrayList<>();
            ClientPlayNetworking.registerGlobalReceiver(Utils.ADDON_CHANNEL, ((client, handler, buf, sender) -> {
                final String utf = ByteStreams.newDataInput(buf.getWrittenBytes()).readUTF();
                if (utf.equals("clear")) {
                    normalAddons.addAll(ModConfig.getAddons());
                    ModConfig.getAddons().clear();
                    return;
                }

                ModConfig.getAddons().add(utf);
            }));

            ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
                if (!normalAddons.isEmpty()) {
                    ModConfig.getAddons().clear();
                    ModConfig.getAddons().addAll(normalAddons);
                    normalAddons.clear();
                }
            }));
        }
        
        if (ModConfig.shouldUseCustomTextures() && Utils.isMoreBlockPredicatesEnabled()) {
            ClientPlayNetworking.registerGlobalReceiver(Utils.BLOCK_CHANNEL, ((client, handler, buf, sender) -> {
                final ByteArrayDataInput packet = ByteStreams.newDataInput(buf.getWrittenBytes());
                final int x = packet.readInt();
                final int y = packet.readInt();
                final int z = packet.readInt();
                final String id = packet.readUTF();
                final BlockPos blockPos = new BlockPos(x, y, z);
                if (id.equals(" ")) {
                    ResourceLoader.removePlacedBlock(blockPos);
                    return;
                }
                ResourceLoader.addPlacedBlock(blockPos, id.toLowerCase());
            }));

            ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
                final PacketByteBuf packetByteBuf = PacketByteBufs.create();
                final ChunkPos chunkPos = chunk.getPos();
                packetByteBuf.writeInt(chunkPos.x);
                packetByteBuf.writeInt(chunkPos.z);
                ClientPlayNetworking.send(Utils.BLOCK_CHANNEL, packetByteBuf);
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
        return path.endsWith(".json");
    }
    
    public static boolean filterAddons(Identifier identifier) {
        if (!filterResources(identifier)) {
            return false;
        }

        final String path = identifier.getPath();
        final String addon = getFileName(path);
        for (String enabledAddon : ModConfig.getAddons()) {
            if (enabledAddon.equalsIgnoreCase(addon)) {
                return true;
            }
        }
        
        return false;
    }

    public static boolean filterItems(Identifier identifier) {
        if (!filterResources(identifier)) {
            return false;
        }

        final String path = identifier.getPath();
        final String id = getFileName(path);
        return ResourceLoader.getSlimefunItems().containsKey(id.toUpperCase()) || id.equals("supreme_ventus_generator");
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));
    }
}
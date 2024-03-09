package me.justahuman.slimefun_essentials;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Channels;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
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
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
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
        
        if (CompatUtils.isClothConfigLoaded()) {
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
                ResourceLoader.loadResources(manager);
            }
        });
        
        if (ModConfig.autoToggleAddons()) {
            final List<String> normalAddons = new ArrayList<>();
            ClientPlayNetworking.registerGlobalReceiver(Channels.ADDON_CHANNEL, ((client, handler, buf, sender) -> {
                final String utf = ByteStreams.newDataInput(buf.array()).readUTF();
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
        
        if (ModConfig.blockFeatures()) {
            ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
                final PacketByteBuf packetByteBuf = PacketByteBufs.create();
                final ChunkPos chunkPos = chunk.getPos();
                packetByteBuf.writeInt(chunkPos.x);
                packetByteBuf.writeInt(chunkPos.z);
                ClientPlayNetworking.send(Channels.BLOCK_CHANNEL, packetByteBuf);
            }));

            ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ResourceLoader.removePlacedChunk(chunk.getPos()));

            ClientPlayNetworking.registerGlobalReceiver(Channels.BLOCK_CHANNEL, ((client, handler, buf, sender) -> {
                final ByteArrayDataInput packet = ByteStreams.newDataInput(buf.array());
                final int x = packet.readInt();
                final int y = packet.readInt();
                final int z = packet.readInt();
                final String id = packet.readUTF();
                final BlockPos blockPos = new BlockPos(x, y, z);

                // If the id is a space that means it's no longer a slimefun block
                if (id.equals(" ")) {
                    ResourceLoader.removePlacedBlock(blockPos);
                    return;
                }

                ResourceLoader.addPlacedBlock(blockPos, id.toLowerCase());
            }));

            ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraftClient) -> ResourceLoader.clearPlacedBlocks());
        }
    }
}
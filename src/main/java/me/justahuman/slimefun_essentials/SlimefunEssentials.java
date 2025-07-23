package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.api.RecipeDisplay;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.payload.ComponentTypePayload;
import me.justahuman.slimefun_essentials.client.payload.ItemsPayload;
import me.justahuman.slimefun_essentials.client.payload.LoadingStatePayload;
import me.justahuman.slimefun_essentials.client.payload.RecipeCategoriesPayload;
import me.justahuman.slimefun_essentials.client.payload.RecipeDisplayPayload;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Payloads;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class SlimefunEssentials implements ClientModInitializer {
    public static final String MOD_ID = "slimefun_essentials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(Payloads.LOADING_STATE_CHANNEL, LoadingStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.ITEM_CHANNEL, ItemsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.COMPONENT_TYPE_CHANNEL, ComponentTypePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.RECIPE_CATEGORIES_CHANNEL, RecipeCategoriesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.RECIPE_DISPLAY_CHANNEL, RecipeDisplayPayload.CODEC);
        ModConfig.loadConfig();

        ClientPlayNetworking.registerGlobalReceiver(Payloads.LOADING_STATE_CHANNEL, (payload, context) -> {
            Payloads.expect(payload);
        });
        ClientPlayNetworking.registerGlobalReceiver(Payloads.ITEM_CHANNEL, (payload, context) -> {
            if (ModConfig.recipeFeatures() && payload != ItemsPayload.EMPTY) {
                payload.load();
                Payloads.checkMetExpected();
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(Payloads.COMPONENT_TYPE_CHANNEL, (payload, context) -> {});
        ClientPlayNetworking.registerGlobalReceiver(Payloads.RECIPE_CATEGORIES_CHANNEL, (payload, context) -> {});
        ClientPlayNetworking.registerGlobalReceiver(Payloads.RECIPE_DISPLAY_CHANNEL, (payload, context) -> {});

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            RecipeCategory.clear();
            DisplayComponentType.clear();
            RecipeDisplay.clear();
            SlimefunRegistry.reset();
            Payloads.reset();
        });

        if (CompatUtils.isClothConfigLoaded()) {
            final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("slimefun_essentials.key_bind.open_config", GLFW.GLFW_KEY_F6, "slimefun_essentials.title"));
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (keyBinding.isPressed()) {
                    client.setScreen(ConfigScreen.buildScreen(client.currentScreen));
                }
            });
        }
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path.toLowerCase(Locale.ROOT));
    }
}
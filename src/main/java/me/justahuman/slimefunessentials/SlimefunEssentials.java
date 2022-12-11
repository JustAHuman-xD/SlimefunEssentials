package me.justahuman.slimefunessentials;

import me.justahuman.slimefunessentials.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SlimefunEssentials implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        final FabricLoader instance = FabricLoader.getInstance();
        if (instance.isModLoaded("clothconfig-2")) {
            AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        }
    }
}
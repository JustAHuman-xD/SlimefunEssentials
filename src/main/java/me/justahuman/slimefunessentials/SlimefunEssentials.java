package me.justahuman.slimefunessentials;

import me.justahuman.slimefunessentials.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class SlimefunEssentials implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        if (Utils.isClothConfigEnabled()) {
            AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        }
    }
}
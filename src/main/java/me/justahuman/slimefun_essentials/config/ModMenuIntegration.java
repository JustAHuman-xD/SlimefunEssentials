package me.justahuman.slimefun_essentials.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> CompatUtils.isClothConfigLoaded() ? ConfigScreen.buildScreen(parent) : parent;
    }
}
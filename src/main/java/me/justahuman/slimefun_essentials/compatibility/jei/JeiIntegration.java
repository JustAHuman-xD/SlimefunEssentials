package me.justahuman.slimefun_essentials.compatibility.jei;

import mezz.jei.api.IModPlugin;
import net.minecraft.util.Identifier;

public class JeiIntegration implements IModPlugin {

    @Override
    public Identifier getPluginUid() {

        return new Identifier("slimefun_essentials", "jei_plugin");
    }


}

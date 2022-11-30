package me.justahuman.slimefuntoemi.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "sftoemi")
public
class ModConfig implements ConfigData {
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    boolean useMachineDefaults = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableInfinityExpansion = false;

    public boolean getUseMachineDefaults() {
        return useMachineDefaults;
    }

    public boolean getEnableInfinityExpansion() {
        return enableInfinityExpansion;
    }
}
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
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableSpiritsUnchained = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableEcoPower = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableElectricSpawners = false;

    public boolean useMachineDefaults() {
        return useMachineDefaults;
    }
    public boolean enableInfinityExpansion() {
        return enableInfinityExpansion;
    }
    public boolean enableElectricSpawners() {
        return enableElectricSpawners;
    }
    public boolean enableEcoPower() {
        return enableEcoPower;
    }
    public boolean enableSpiritsUnchained() {
        return enableSpiritsUnchained;
    }
}
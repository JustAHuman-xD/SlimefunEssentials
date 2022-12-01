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
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableExoticGarden = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableExtraGear = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableHotbarPets = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableLuckyBlocks = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableSlimyTreeTaps = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableSoulJars = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableDankTech = false;
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean enableTransCendence = false;

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
    public boolean enableExoticGarden() {
        return enableExoticGarden;
    }
    public boolean enableExtraGear() {
        return enableExtraGear;
    }
    public boolean enableHotbarPets() {
        return enableHotbarPets;
    }
    public boolean enableLuckyBlocks() {
        return enableLuckyBlocks;
    }
    public boolean enableSlimyTreeTaps() {
        return enableSlimyTreeTaps;
    }
    public boolean enableSoulJars() {
        return enableSoulJars;
    }
    public boolean enableSpiritsUnchained() {
        return enableSpiritsUnchained;
    }
    public boolean enableDankTech() {
        return enableDankTech;
    }
    public boolean enableTransCendence() {
        return enableTransCendence;
    }
}
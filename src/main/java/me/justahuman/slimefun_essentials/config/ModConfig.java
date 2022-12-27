package me.justahuman.slimefun_essentials.config;

import lombok.Getter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "slimefun_essentials")
public
class ModConfig implements ConfigData {
    //General Options
    @Getter
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    boolean machineDefaults = false;
    
    //Addon Options
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean globalWarmingEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean miniblocksEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean networksEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean crystamaeHistoriaEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean extraToolsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean foxyMachinesEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean relicsOfCthoniaEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean fnAmplificationsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean simpleMaterialGeneratorsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean dynaTechEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean dyeBenchEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean supremeEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean wilderNetherEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean compressionCraftEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean villagerUtilEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean mobCapturerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean emcTechEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean flowerTechEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean slimeTinkerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean galactifunEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean fluffyMachinesEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean alchimiaVitaeEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean slimefunWarfareEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean liquidEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean liteXpansionEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean simpleUtilsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean elementManipulationEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean netheopoiesisEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean simpleStorageEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean slimyRepairEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean infinityExpansionEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean spiritsUnchainedEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean ecoPowerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean electricSpawnersEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean exoticGardenEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean extraGearEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean hotbarPetsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean luckyBlocksEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean slimyTreeTapsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean soulJarsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean dankTech2Enabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    @ConfigEntry.Gui.Tooltip
    boolean transcendenceEnabled = false;
}
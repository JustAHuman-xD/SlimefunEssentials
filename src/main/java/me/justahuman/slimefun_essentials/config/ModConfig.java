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
    boolean machineDefaults = false;
    
    //Addon Options
    @Getter
    @ConfigEntry.Category("addons")
    boolean alchimiaVitaeEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean compressionCraftEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean crystamaeHistoriaEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean dankTech2Enabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean dyeBenchEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean dynaTechEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean ecoPowerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean electricSpawnersEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean elementManipulationEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean emcTechEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean exoticGardenEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean extraGearEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean extraToolsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean flowerPowerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean fluffyMachinesEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean fnAmplificationsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean foxyMachinesEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean galactifunEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean globalWarmingEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean hotbarPetsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean infinityExpansionEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean liquidEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean liteXpansionEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean luckyBlocksEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean miniblocksEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean mobCapturerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean netheopoiesisEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean networksEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean relicsOfCthoniaEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean simpleMaterialGeneratorsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean simpleStorageEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean simpleUtilsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean slimeTinkerEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean slimefunWarfareEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean slimyRepairEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean slimyTreeTapsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean soulJarsEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean spiritsUnchainedEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean supremeEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean transcendenceEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean villagerUtilEnabled = false;
    @Getter
    @ConfigEntry.Category("addons")
    boolean wilderNetherEnabled = false;
}
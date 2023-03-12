package me.justahuman.slimefun_essentials.utils;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static final String ID = "slimefun_essentials";
    public static final Identifier WIDGETS = new Identifier(ID, "textures/gui/widgets.png");
    private static final Logger logger = LoggerFactory.getLogger(ID);
    private static final String errorMessage = "[SFE] Failed to load data";
    
    public static Identifier newIdentifier(String namespace) {
        return new Identifier(ID, namespace.toLowerCase());
    }

    public static boolean isClothConfigEnabled() {
        return FabricLoader.getInstance().isModLoaded("cloth-config2");
    }
    
    public static boolean isItemGroupEnabled() {
        if (isClothConfigEnabled()) {
            final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            return modConfig.isItemGroupEnabled();
        }
        
        return true;
    }
    
    public static List<String> getEnabledAddons() {
        final List<String> addonList = new ArrayList<>(Collections.singletonList("slimefun"));
        if (isClothConfigEnabled()) {
            final ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            
            if (modConfig.isAlchimiaVitaeEnabled()) {
                addonList.add("alchimia_vitae");
            }
            if (modConfig.isCompressionCraftEnabled()) {
                addonList.add("compression_craft");
            }
            if (modConfig.isCrystamaeHistoriaEnabled()) {
                addonList.add("crystamae_historia");
            }
            if (modConfig.isDankTech2Enabled()) {
                addonList.add("dank_tech_2");
            }
            if (modConfig.isDyeBenchEnabled()) {
                addonList.add("dye_bench");
            }
            if (modConfig.isDynaTechEnabled()) {
                addonList.add("dyna_tech");
            }
            if (modConfig.isEcoPowerEnabled()) {
                addonList.add("eco_power");
            }
            if (modConfig.isElectricSpawnersEnabled()) {
                addonList.add("electric_spawners");
            }
            if (modConfig.isElementManipulationEnabled()) {
                addonList.add("element_manipulation");
            }
            if (modConfig.isEmcTechEnabled()) {
                addonList.add("emc_tech");
            }
            if (modConfig.isExoticGardenEnabled()) {
                addonList.add("exotic_garden");
            }
            if (modConfig.isExtraGearEnabled()) {
                addonList.add("extra_gear");
            }
            if (modConfig.isExtraToolsEnabled()) {
                addonList.add("extra_tools");
            }
            if (modConfig.isFlowerPowerEnabled()) {
                addonList.add("flower_power");
            }
            if (modConfig.isFluffyMachinesEnabled()) {
                addonList.add("fluffy_machines");
            }
            if (modConfig.isFnAmplificationsEnabled()) {
                addonList.add("fn_amplifications");
            }
            if (modConfig.isFoxyMachinesEnabled()) {
                addonList.add("foxy_machines");
            }
            if (modConfig.isGalactifunEnabled()) {
                addonList.add("galactifun");
            }
            if (modConfig.isGlobalWarmingEnabled()) {
                addonList.add("global_warming");
            }
            if (modConfig.isHotbarPetsEnabled()) {
                addonList.add("hotbar_pets");
            }
            if (modConfig.isInfinityExpansionEnabled()) {
                addonList.add("infinity_expansion");
            }
            if (modConfig.isLiquidEnabled()) {
                addonList.add("liquid");
            }
            if (modConfig.isLiteXpansionEnabled()) {
                addonList.add("lite_xpansion");
            }
            if (modConfig.isLuckyBlocksEnabled()) {
                addonList.add("lucky_blocks");
            }
            if (modConfig.isMiniblocksEnabled()) {
                addonList.add("miniblocks");
            }
            if (modConfig.isMobCapturerEnabled()) {
                addonList.add("mob_capturer");
            }
            if (modConfig.isNetheopoiesisEnabled()) {
                addonList.add("netheopoiesis");
            }
            if (modConfig.isNetworksEnabled()) {
                addonList.add("networks");
            }
            if (modConfig.isRelicsOfCthoniaEnabled()) {
                addonList.add("relics_of_cthonia");
            }
            if (modConfig.isSimpleMaterialGeneratorsEnabled()) {
                addonList.add("simple_material_generators");
            }
            if (modConfig.isSimpleStorageEnabled()) {
                addonList.add("simple_storage");
            }
            if (modConfig.isSimpleUtilsEnabled()) {
                addonList.add("simple_utils");
            }
            if (modConfig.isSlimeTinkerEnabled()) {
                addonList.add("slime_tinker");
            }
            if (modConfig.isSlimefunWarfareEnabled()) {
                addonList.add("slimefun_warfare");
            }
            if (modConfig.isSlimyRepairEnabled()) {
                addonList.add("slimy_repair");
            }
            if (modConfig.isSlimyTreeTapsEnabled()) {
                addonList.add("slimy_tree_taps");
            }
            if (modConfig.isSoulJarsEnabled()) {
                addonList.add("soul_jars");
            }
            if (modConfig.isSpiritsUnchainedEnabled()) {
                addonList.add("spirits_unchained");
            }
            if (modConfig.isSupremeEnabled()) {
                addonList.add("supreme");
            }
            if (modConfig.isTranscendenceEnabled()) {
                addonList.add("transcendence");
            }
            if (modConfig.isVillagerUtilEnabled()) {
                addonList.add("villager_util");
            }
            if (modConfig.isWilderNetherEnabled()) {
                addonList.add("wilder_nether");
            }
        }
        
        return addonList;
    }
    
    public static void fillInputs(List<EmiIngredient> list, int size) {
        for (int i = list.size(); i <= size; i++) {
            list.add(EmiStack.EMPTY);
        }
    }
    
    public static void fillOutputs(List<EmiStack> list, int size) {
        for (int i = list.size(); i <= size; i++) {
            list.add(EmiStack.EMPTY);
        }
    }
    
    public static void log(String message) {
        logger.info(message);
    }
    
    public static void warn(String warning) {
        logger.warn(warning);
    }
    
    public static void error(Exception exception) {
        logger.error(errorMessage);
        exception.printStackTrace();
    }
}

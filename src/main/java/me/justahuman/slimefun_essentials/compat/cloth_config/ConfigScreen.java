package me.justahuman.slimefun_essentials.compat.cloth_config;

import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen {
    public static Screen buildScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("slimefun_essentials.title"));
        
        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory category = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.title"));
        
        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.block_features"), ModConfig.blockFeatures())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.block_features.tooltip"))
                .setRequirement(CompatUtils::isBlockFeatureModLoaded)
                .setSaveConsumer(ModConfig::setBlockFeatures)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.recipe_features"), ModConfig.recipeFeatures())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.recipe_features.tooltip"))
                .setRequirement(CompatUtils::isRecipeModLoaded)
                .setSaveConsumer(ModConfig::setRecipeFeatures)
                .build());
        
        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.auto_toggle_addons"), ModConfig.autoToggleAddons())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.auto_toggle_addons.tooltip"))
                .setSaveConsumer(ModConfig::setAutoToggleAddons)
                .build());
        
        category.addEntry(entryBuilder.startStrList(Text.translatable("slimefun_essentials.config.option.addons"), ModConfig.getAddons())
                .setDefaultValue(new ArrayList<>(List.of("Slimefun")))
                .setTooltip(Text.translatable("slimefun_essentials.config.option.addons.tooltip"))
                .setSaveConsumer(ModConfig::setAddons)
                .build());
        
        builder.setSavingRunnable(ModConfig::saveConfig);
        
        return builder.build();
    }
}
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
        final ConfigCategory generalCategory = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.config.category.general"));
        final ConfigCategory addonsCategory = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.config.category.addons"));
        final ConfigCategory serverWhitelistCategory = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.config.category.server_whitelist"));
        
        generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.block_features"), ModConfig.blockFeatures())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.block_features.tooltip"))
                .setRequirement(CompatUtils::isBlockFeatureModLoaded)
                .setSaveConsumer(ModConfig::setBlockFeatures)
                .build());

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.recipe_features"), ModConfig.recipeFeatures())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.recipe_features.tooltip"))
                .setRequirement(CompatUtils::isRecipeModLoaded)
                .setSaveConsumer(ModConfig::setRecipeFeatures)
                .build());

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.require_server_connection"), ModConfig.requireServerConnection())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.require_server_connection.tooltip"))
                .setSaveConsumer(ModConfig::setRequireServerConnection)
                .build());
        
        addonsCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.auto_toggle_addons"), ModConfig.autoToggleAddons())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.auto_toggle_addons.tooltip"))
                .setSaveConsumer(ModConfig::setAutoToggleAddons)
                .build());
        
        addonsCategory.addEntry(entryBuilder.startStrList(Text.translatable("slimefun_essentials.config.option.addons"), ModConfig.getAddons())
                .setDefaultValue(new ArrayList<>(List.of("Slimefun")))
                .setTooltip(Text.translatable("slimefun_essentials.config.option.addons.tooltip"))
                .setSaveConsumer(ModConfig::setAddons)
                .build());

        serverWhitelistCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.enable_server_whitelist"), ModConfig.enableServerWhitelist())
                .setDefaultValue(false)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.enable_server_whitelist.tooltip"))
                .setSaveConsumer(ModConfig::setEnableServerWhitelist)
                .build());

        serverWhitelistCategory.addEntry(entryBuilder.startStrList(Text.translatable("slimefun_essentials.config.option.server_whitelist"), ModConfig.getEnabledServers())
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Text.translatable("slimefun_essentials.config.option.server_whitelist.tooltip"))
                .setSaveConsumer(ModConfig::setEnabledServers)
                .build());
        
        builder.setSavingRunnable(ModConfig::saveConfig);
        
        return builder.build();
    }
}
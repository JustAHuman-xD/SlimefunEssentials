package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.categorys.AncientAltarCategory;
import me.justahuman.slimefun_essentials.compat.rei.categorys.GridCategory;
import me.justahuman.slimefun_essentials.compat.rei.categorys.ProcessCategory;
import me.justahuman.slimefun_essentials.compat.rei.categorys.ReactorCategory;
import me.justahuman.slimefun_essentials.compat.rei.categorys.SmelteryCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.AncientAltarDisplay;
import me.justahuman.slimefun_essentials.compat.rei.displays.GridDisplay;
import me.justahuman.slimefun_essentials.compat.rei.displays.ProcessDisplay;
import me.justahuman.slimefun_essentials.compat.rei.displays.ReactorDisplay;
import me.justahuman.slimefun_essentials.compat.rei.displays.SmelteryDisplay;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

public class ReiIntegration implements REIClientPlugin {
    public static final EntryType<SlimefunItemStack> SLIMEFUN = EntryType.deferred(Utils.newIdentifier("rei_slimefun"));
    public static final ReiRecipeInterpreter RECIPE_INTERPRETER = new ReiRecipeInterpreter();

    @Override
    public double getPriority() {
        return 1;
    }

    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(SLIMEFUN, new SlimefunEntryDefinition());
    }
    
    @Override
    public void registerEntries(EntryRegistry registry) {
        for (SlimefunItemStack slimefunItemStack : ResourceLoader.getSlimefunItems().values()) {
            registry.addEntry(EntryStack.of(SLIMEFUN, slimefunItemStack));
        }
    }
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            final ItemStack icon = ResourceLoader.getSlimefunItems().get(slimefunCategory.id()).itemStack();
            final DisplayCategory<?> displayCategory = getReiCategory(slimefunCategory, icon);
            registry.add(displayCategory);
            registry.addWorkstations(displayCategory.getCategoryIdentifier(), EntryStacks.of(icon));
        }
    }
    
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                registry.add(getDisplay(slimefunCategory, slimefunRecipe));
            }
        }
    }

    public static DisplayCategory<?> getReiCategory(SlimefunCategory slimefunCategory, ItemStack icon) {
        final String type = slimefunCategory.type();
        if (type.equals("ancient_altar")) {
            return new AncientAltarCategory(slimefunCategory, icon);
        } else if (type.equals("smeltery")) {
            return new SmelteryCategory(slimefunCategory, icon);
        } else if (type.equals("reactor")) {
            return new ReactorCategory(slimefunCategory, icon);
        } else if (type.contains("grid")) {
            return new GridCategory(slimefunCategory, icon, TextureUtils.getSideSafe(type));
        } else {
            return new ProcessCategory(slimefunCategory, icon);
        }
    }

    public static Display getDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        final String type = slimefunCategory.type();
        if (type.equals("ancient_altar")) {
            return new AncientAltarDisplay(slimefunCategory, slimefunRecipe);
        } else if (type.equals("smeltery")) {
            return new SmelteryDisplay(slimefunCategory, slimefunRecipe);
        } else if (type.equals("reactor")) {
            return new ReactorDisplay(slimefunCategory, slimefunRecipe);
        } else if (type.contains("grid")) {
            return new GridDisplay(slimefunCategory, slimefunRecipe, TextureUtils.getSideSafe(type));
        } else {
            return new ProcessDisplay(slimefunCategory, slimefunRecipe);
        }
    }

    public static Widget widgetFromSlimefunLabel(SlimefunLabel slimefunLabel, int x, int y) {
        return Widgets.createDrawableWidget((helper, stack, mouseX, mouseY, delta) -> slimefunLabel.draw(stack, x, y, REIRuntime.getInstance().isDarkThemeEnabled()));
    }

    public static EntryStack<ItemStack> unwrap(EntryStack<SlimefunItemStack> entryStack) {
        EntryStack<ItemStack> from = EntryStacks.of(entryStack.getValue().itemStack());
        from.setZ(entryStack.getZ());
        return from;
    }
}

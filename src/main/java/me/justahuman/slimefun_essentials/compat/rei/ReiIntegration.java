package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.categories.ProcessCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.ProcessDisplay;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.stream.Collectors;

public class ReiIntegration implements REIClientPlugin {
    public static final EntryType<SlimefunItemStack> SLIMEFUN = EntryType.deferred(Utils.newIdentifier("rei_slimefun"));
    public static final ReiRecipeInterpreter RECIPE_INTERPRETER = new ReiRecipeInterpreter();

    public static Widget widgetFromSlimefunLabel(SlimefunLabel slimefunLabel, int x, int y) {
        return Widgets.createDrawableWidget((helper, stack, mouseX, mouseY, delta) -> slimefunLabel.draw(stack, x, y, REIRuntime.getInstance().isDarkThemeEnabled()));
    }

    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(SLIMEFUN, new SlimefunEntryDefinition());
    }
    
    @Override
    public void registerEntries(EntryRegistry registry) {
        final Collection<ItemStack> slimefunStacks = ResourceLoader.getSlimefunItems().values().stream().map(SlimefunItemStack::itemStack).collect(Collectors.toSet());
        registry.removeEntryIf(entryStack -> entryStack.getType() == VanillaEntryTypes.ITEM && slimefunStacks.contains((ItemStack) entryStack.getValue()));
        for (SlimefunItemStack slimefunItemStack : ResourceLoader.getSlimefunItems().values()) {
            registry.addEntry(EntryStack.of(SLIMEFUN, slimefunItemStack));
        }
    }
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            registry.add(new ProcessCategory(slimefunCategory, ResourceLoader.getSlimefunItems().get(slimefunCategory.id()).itemStack()));
        }
    }
    
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                registry.add(new ProcessDisplay(slimefunCategory, slimefunRecipe));
            }
        }
    }
}

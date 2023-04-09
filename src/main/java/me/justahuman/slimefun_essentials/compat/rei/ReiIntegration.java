package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
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
            registry.add(getReiCategory(slimefunCategory, ResourceLoader.getSlimefunItems().get(slimefunCategory.id()).itemStack()));
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

    public static DisplayCategory<?> getReiCategory(SlimefunCategory slimefunCategory, ItemStack icon) {
        final String type = slimefunCategory.type();
        if (type.equals("ancient_altar")) {
            return new SlimefunReiCategory<AncientAltarDisplay>(RecipeRenderer.Type.ANCIENT_ALTAR, slimefunCategory, icon);
        } else if (type.equals("smeltery")) {
            return new SlimefunReiCategory<SmelteryDisplay>(RecipeRenderer.Type.SMELTERY, slimefunCategory, icon);
        } else if (type.equals("reactor")) {
            return new SlimefunReiCategory<ReactorDisplay>(RecipeRenderer.Type.REACTOR, slimefunCategory, icon);
        } else if (type.contains("grid")) {
            return new SlimefunReiCategory<GridDisplay>(RecipeRenderer.Type.GRID(TextureUtils.getSideSafe(type)), slimefunCategory, icon);
        } else {
            return new SlimefunReiCategory<ProcessDisplay>(RecipeRenderer.Type.PROCESS, slimefunCategory, icon);
        }
    }

    public static Widget widgetFromSlimefunLabel(SlimefunLabel slimefunLabel, int x, int y) {
        return Widgets.createDrawableWidget((helper, stack, mouseX, mouseY, delta) -> slimefunLabel.draw(stack, x, y, REIRuntime.getInstance().isDarkThemeEnabled()));
    }
}

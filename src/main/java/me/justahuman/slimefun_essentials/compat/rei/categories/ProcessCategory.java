package me.justahuman.slimefun_essentials.compat.rei.categories;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.displays.ProcessDisplay;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProcessCategory implements DisplayCategory<ProcessDisplay> {
    protected final SlimefunCategory slimefunCategory;
    protected final ItemStack icon;
    
    public ProcessCategory(SlimefunCategory slimefunCategory, ItemStack icon) {
        this.slimefunCategory = slimefunCategory;
        this.icon = icon;
    }
    
    @Override
    @NotNull
    public CategoryIdentifier<ProcessDisplay> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.newIdentifier(slimefunCategory.id().toLowerCase()));
    }
    
    @Override
    public Text getTitle() {
        return Text.translatable("emi.category.slimefun", icon.getName());
    }
    
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(icon);
    }
    
    @Override
    public int getDisplayHeight() {
        for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
            if (!slimefunRecipe.outputs().isEmpty()) {
                return TextureUtils.bigSlot;
            }
        }
        return TextureUtils.slot;
    }
    
    @Override
    public int getDisplayWidth(ProcessDisplay display) {
        return display.getDisplayWidth();
    }
    
    @Override
    public List<Widget> setupDisplay(ProcessDisplay display, Rectangle bounds) {
        
        return DisplayCategory.super.setupDisplay(display, bounds);
    }
}
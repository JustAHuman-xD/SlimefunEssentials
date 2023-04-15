package me.justahuman.slimefun_essentials.compat.rei.categorys;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.api.RecipeRenderer;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.SlimefunDisplay;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlimefunReiCategory<T extends SlimefunDisplay> extends RecipeRenderer implements DisplayCategory<T> {
    protected final SlimefunCategory slimefunCategory;
    protected final ItemStack icon;

    public SlimefunReiCategory(Type type, SlimefunCategory slimefunCategory, ItemStack icon) {
        super(type);

        this.slimefunCategory = slimefunCategory;
        this.icon = icon;
    }

    @Override
    @NotNull
    public CategoryIdentifier<T> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.newIdentifier(this.slimefunCategory.id().toLowerCase()));
    }

    @NotNull
    public Text getTitle() {
        return Text.translatable("slimefun_essentials.recipe.category.slimefun", this.icon.getName());
    }

    @NotNull
    public Renderer getIcon() {
        return EntryStacks.of(this.icon);
    }

    @Override
    public int getDisplayHeight() {
        return getDisplayHeight(this.slimefunCategory) + TextureUtils.reiPadding;
    }

    @Override
    public int getDisplayWidth(T display) {
        return getDisplayWidth(display.slimefunRecipe()) + TextureUtils.reiPadding;
    }

    @Override
    public List<Widget> setupDisplay(T display, Rectangle bounds) {
        final int x = bounds.getMinX() + ((getDisplayWidth(display) - getContentsWidth(display.slimefunRecipe())) / 2);
        final int y = bounds.getMinY() + ((getDisplayHeight() - getDisplayHeight(display.slimefunRecipe())) / 2);
        return display.setupDisplay(new OffsetBuilder(display, display.slimefunRecipe(), x, y, y), new ArrayList<>(List.of(Widgets.createCategoryBase(bounds))), bounds, this.icon);
    }
}

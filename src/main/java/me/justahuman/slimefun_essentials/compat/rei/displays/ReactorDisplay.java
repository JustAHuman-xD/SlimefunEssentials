package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.ReiUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ReactorDisplay extends ProcessDisplay {
    public ReactorDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        super(Type.REACTOR, slimefunCategory, slimefunRecipe);

        ReiUtils.fillEntrys(this.inputs, 4);
    }

    @Override
    public List<Widget> setupDisplay(OffsetBuilder offsets, List<Widget> widgets, Rectangle bounds, ItemStack icon) {
        addSlot(widgets, this.inputs.get(0), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        addSlot(widgets, EntryIngredient.empty(), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        addSlot(widgets, EntryIngredient.empty(), offsets.getX(), offsets.getY());
        offsets.x().addSlot();

        addArrow(widgets, offsets.getX(), offsets.getY(), false);
        offsets.x().addArrow();

        if (this.slimefunRecipe.hasOutputs()) {
            widgets.add(Widgets.createSlot(new Point(offsets.getX() + 5, offsets.getY() + 5)).entries(this.outputs.get(0)).disableBackground().markOutput());
            widgets.add(Widgets.createResultSlotBackground(new Point(offsets.getX() + 5, offsets.getY() + 5)));
        }

        if (this.slimefunRecipe.hasEnergy()) {
            addEnergy(widgets, offsets.getX() + (this.slimefunRecipe.hasOutputs() ? (TextureUtils.outputSize - TextureUtils.energyWidth) / 2 : 0), offsets.getY() + (this.slimefunRecipe.hasOutputs() ? - TextureUtils.energyHeight - TextureUtils.padding : TextureUtils.padding));
            offsets.x().add(this.slimefunRecipe.hasOutputs() ? TextureUtils.outputSize : TextureUtils.energyWidth).addPadding();
        }

        addArrow(widgets, offsets.getX(), offsets.getY(), true);
        offsets.x().addArrow();
        offsets.y().subtract(TextureUtils.slotSize * 2);
        addSlot(widgets, inputs.get(1), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        addSlot(widgets, inputs.get(2), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        addSlot(widgets, inputs.get(3), offsets.getX(), offsets.getY());

        return widgets;
    }
}

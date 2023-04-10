package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.ReiUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GridDisplay extends ProcessDisplay {
    protected final int side;
    public GridDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe, int side) {
        super(Type.GRID(side), slimefunCategory, slimefunRecipe);

        this.side = side;
        ReiUtils.fillEntrys(this.inputs, side * side);
    }

    @Override
    public List<Widget> setupDisplay(OffsetBuilder offsets, List<Widget> widgets, Rectangle bounds, ItemStack icon) {
        // Display Energy
        addEnergyWithCheck(widgets, offsets);

        int i = 0;
        for (int y = 1; y <= this.side; y++) {
            for (int x = 1; x <= this.side; x++) {
                addSlot(widgets, this.inputs.get(i), offsets.getX(), offsets.getY());
                offsets.x().addSlot(false);
                i++;
            }
            offsets.x().subtract(TextureUtils.slotSize * this.side);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.slotSize * this.side).addPadding();

        // Display Arrow
        addArrow(widgets, offsets);

        // Display Outputs
        addOutputsOrEnergy(widgets, offsets);
        return widgets;
    }
}

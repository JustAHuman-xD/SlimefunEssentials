package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.ReiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AncientAltarDisplay extends ProcessDisplay {
    public AncientAltarDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        super(Type.ANCIENT_ALTAR, slimefunCategory, slimefunRecipe);
    }

    @Override
    public List<Widget> setupDisplay(OffsetBuilder ignored, List<Widget> widgets, Rectangle bounds, ItemStack icon) {
        final OffsetBuilder offsets = new OffsetBuilder(this, this.slimefunRecipe, calculateXOffset(this.slimefunRecipe), 0, bounds.getMinY());

        addSlot(widgets, this.inputs.get(3), offsets.getX(), offsets.slot(), false);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot()));
        offsets.x().addSlot(false);
        addSlot(widgets, this.inputs.get(0), offsets.getX(), offsets.slot() + TextureUtils.slotSize);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot() + TextureUtils.slotSize));
        addSlot(widgets, this.inputs.get(6), offsets.getX(), offsets.slot() - TextureUtils.slotSize);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot() - TextureUtils.slotSize));
        offsets.x().addSlot(false);
        addSlot(widgets, this.inputs.get(1), offsets.getX(), offsets.slot() + TextureUtils.slotSize);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot() + TextureUtils.slotSize));
        addSlot(widgets, this.inputs.get(4), offsets.getX(), offsets.slot());
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.ALTAR, offsets.getX(), offsets.slot()));
        addSlot(widgets, this.inputs.get(7), offsets.getX(), offsets.slot() - TextureUtils.slotSize);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot() - TextureUtils.slotSize));
        offsets.x().addSlot(false);
        addSlot(widgets, this.inputs.get(2), offsets.getX(), offsets.slot() + TextureUtils.slotSize);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot() + TextureUtils.slotSize));
        addSlot(widgets, this.inputs.get(8), offsets.getX(), offsets.slot() - TextureUtils.slotSize);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot() - TextureUtils.slotSize));
        offsets.x().addSlot(false);
        addSlot(widgets, this.inputs.get(5), offsets.getX(), offsets.slot(), false);
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.PEDESTAL, offsets.getX(), offsets.slot()));
        offsets.x().addSlot();
        addArrow(widgets, offsets);
        addSlot(widgets, offsets, this.outputs.get(0));

        return widgets;
    }
}

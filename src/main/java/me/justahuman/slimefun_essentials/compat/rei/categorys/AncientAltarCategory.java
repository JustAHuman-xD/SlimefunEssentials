package me.justahuman.slimefun_essentials.compat.rei.categorys;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.AncientAltarDisplay;
import net.minecraft.item.ItemStack;

public class AncientAltarCategory extends SlimefunReiCategory<AncientAltarDisplay> {
    public AncientAltarCategory(SlimefunCategory slimefunCategory, ItemStack icon) {
        super(Type.ANCIENT_ALTAR, slimefunCategory, icon);
    }
}

package me.justahuman.slimefun_essentials.compat.rei.categorys;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.ReactorDisplay;
import net.minecraft.item.ItemStack;

public class ReactorCategory extends SlimefunReiCategory<ReactorDisplay> {
    public ReactorCategory(SlimefunCategory slimefunCategory, ItemStack icon) {
        super(Type.REACTOR, slimefunCategory, icon);
    }
}

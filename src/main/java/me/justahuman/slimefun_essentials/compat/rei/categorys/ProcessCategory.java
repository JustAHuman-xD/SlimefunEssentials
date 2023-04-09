package me.justahuman.slimefun_essentials.compat.rei.categorys;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.ProcessDisplay;
import net.minecraft.item.ItemStack;

public class ProcessCategory extends SlimefunReiCategory<ProcessDisplay> {
    public ProcessCategory(SlimefunCategory slimefunCategory, ItemStack icon) {
        super(Type.PROCESS, slimefunCategory, icon);
    }
}

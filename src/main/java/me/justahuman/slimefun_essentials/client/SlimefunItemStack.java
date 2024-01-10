package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.item.ItemStack;

public record SlimefunItemStack(String id, ItemStack itemStack) {
    public SlimefunItemStack(ItemStack itemStack) {
        this(Utils.getSlimefunId(itemStack), itemStack);
    }

    public SlimefunItemStack setAmount(int amount) {
        itemStack.setCount(amount);
        return this;
    }
    
    public SlimefunItemStack copy() {
        return new SlimefunItemStack(id, itemStack.copy());
    }
}

package me.justahuman.slimefun_essentials.client;

import net.minecraft.item.ItemStack;

public record SlimefunItemStack(String id, ItemStack itemStack) {
    public SlimefunItemStack setAmount(int amount) {
        itemStack.setCount(amount);
        return this;
    }
    
    public SlimefunItemStack copy() {
        return new SlimefunItemStack(id, itemStack.copy());
    }
}

package me.justahuman.slimefun_essentials.client;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SlimefunItemStack(String id, ItemStack itemStack) {
    public void setCustomModelData(int customModelData) {
        itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(new ArrayList<>(List.of((float) customModelData)), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }
    
    public SlimefunItemStack copy() {
        return new SlimefunItemStack(id, itemStack.copy());
    }
}

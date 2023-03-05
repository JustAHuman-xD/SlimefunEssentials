package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.Utils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemGroups {
    public static void register() {
        
        FabricItemGroup.builder(Utils.newIdentifier("slimefun"))
                .icon(ResourceLoader::getSlimefunHead)
                .entries(((enabledFeatures, entries, operatorEnabled) -> {
                    final List<ItemStack> slimefunItems = new ArrayList<>(ResourceLoader.getSlimefunItems().values());
                    slimefunItems.sort(Comparator.comparing(itemStack -> itemStack.getName().getString()));
                    entries.addAll(slimefunItems);
                }))
                .build();
    }
}
package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.mixins.ItemGroupAccessor;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemGroups {
    private static final Map<Identifier, ItemGroup> ITEM_GROUPS = new HashMap<>();
    private static final Collection<ItemStack> emptyDisplayStacks = ItemStackSet.create();
    private static final Set<ItemStack> emptySearchStacks = ItemStackSet.create();
    
    public static void reset() {
        for (ItemGroup itemGroup : ITEM_GROUPS.values()) {
            ((ItemGroupAccessor) itemGroup).setDisplayStacks(emptyDisplayStacks);
            ((ItemGroupAccessor) itemGroup).setSearchTabStacks(emptySearchStacks);
        }
    }
    
    public static void addItemGroup(String id, ItemStack icon, Set<ItemStack> entries) {
        final Identifier identifier = Utils.newIdentifier(id);
        if (ITEM_GROUPS.containsKey(identifier)) {
            final ItemGroup itemGroup = ITEM_GROUPS.get(identifier);
            ((ItemGroupAccessor) itemGroup).setIcon(icon);
            ((ItemGroupAccessor) itemGroup).setDisplayStacks(entries);
            ((ItemGroupAccessor) itemGroup).setSearchTabStacks(entries);
            return;
        }
        
        ITEM_GROUPS.put(identifier, FabricItemGroup.builder(Utils.newIdentifier(id)).icon(() -> icon).entries(((enabledFeatures, groupEntries, operatorEnabled) -> groupEntries.addAll(entries))).build());
    }
}
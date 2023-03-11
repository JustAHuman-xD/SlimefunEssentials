package me.justahuman.slimefun_essentials.mixins;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;
import java.util.Set;

@Mixin(ItemGroup.class)
public interface ItemGroupAccessor {
    @Accessor("icon")
    void setIcon(ItemStack icon);
    
    @Accessor("displayStacks")
    void setDisplayStacks(Collection<ItemStack> displayStacks);
    
    @Accessor("searchTabStacks")
    void setSearchTabStacks(Set<ItemStack> searchTabStacks);
}

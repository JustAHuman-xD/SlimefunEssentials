package me.justahuman.slimefun_essentials.mixins;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemGroup.class)
public interface ItemGroupAccessor {
    @Accessor("icon")
    void setIcon(ItemStack icon);
}

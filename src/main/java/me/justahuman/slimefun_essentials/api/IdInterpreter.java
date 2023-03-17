package me.justahuman.slimefun_essentials.api;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface IdInterpreter<T> {
    default T interpretId(@NotNull String id, @NotNull T defaultValue) {
        if (id.isEmpty() || id.isBlank()) {
            return defaultValue;
        }
        
        if (!id.contains(":")) {
            Utils.warn("Invalid Ingredient Id:" + id);
            return defaultValue;
        }
        
        final String type = id.substring(0, id.indexOf(":"));
        final String count = id.substring(id.indexOf(":") + 1);
        int amount;
        try {
            amount = Integer.parseInt(count);
        } catch (NumberFormatException ignored) {
            amount = 1;
        }
        
        if (ResourceLoader.getSlimefunItems().containsKey(type)) {
            return fromSlimefunItemStack(ResourceLoader.getSlimefunItems().get(type).copy(), amount, defaultValue);
        }
        
        // Entity
        if (type.startsWith("@")) {
            final Identifier identifier = new Identifier("minecraft:" + type.substring(1));
            if (! Registries.ENTITY_TYPE.containsId(identifier)) {
                Utils.warn("Invalid Ingredient Entity Id: " + id);
                return defaultValue;
            }
            return fromEntityType(Registries.ENTITY_TYPE.get(identifier), amount, defaultValue);
        }
        // Fluid
        else if (type.startsWith("~")) {
            final Identifier identifier = new Identifier("minecraft:" + type.substring(1));
            if (!Registries.FLUID.containsId(identifier)) {
                Utils.warn("Invalid Ingredient Fluid Id: " + id);
                return defaultValue;
            }
            return fromFluid(Registries.FLUID.get(identifier), amount, defaultValue);
        }
        // Tag
        else if (type.startsWith("#")) {
            final Identifier identifier = new Identifier("minecraft:" + type.substring(1));
            return fromTag(TagKey.of(Registries.ITEM.getKey(), identifier), amount, defaultValue);
        }
        // Item (Or Mistake)
        else {
            final Identifier identifier = new Identifier("minecraft:" + type.toLowerCase());
            if (!Registries.ITEM.containsId(identifier)) {
                Utils.warn("Invalid Ingredient ItemStack Id: " + id);
                return defaultValue;
            }
            return fromItemStack(Registries.ITEM.get(identifier).getDefaultStack().copy(), amount, defaultValue);
        }
    }
    
    T fromTag(TagKey<Item> tagKey, int amount, T defaultValue);
    T fromItemStack(ItemStack itemStack, int amount, T defaultValue);
    T fromSlimefunItemStack(SlimefunItemStack slimefunItemStack, int amount, T defaultValue);
    T fromFluid(Fluid fluid, int amount, T defaultValue);
    T fromEntityType(EntityType<?> entityType, int amount, T defaultValue);
}

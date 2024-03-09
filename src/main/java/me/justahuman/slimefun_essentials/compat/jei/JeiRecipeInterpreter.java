package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.api.IdInterpreter;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.fabric.ingredients.fluid.JeiFluidIngredient;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.Optional;

public class JeiRecipeInterpreter implements IdInterpreter<Object> {
    public void addIngredient(IRecipeSlotBuilder slotBuilder, ItemStack itemStack) {
        slotBuilder.addItemStack(itemStack);
    }

    public void addIngredients(IRecipeSlotBuilder slotBuilder, SlimefunRecipeComponent component) {
        for (String id : component.getMultiId() != null ? component.getMultiId() : List.of(component.getId())) {
            addIngredientObject(slotBuilder, interpretId(id, ItemStack.EMPTY));
        }
    }

    public void addIngredientObject(IRecipeSlotBuilder slotBuilder, Object ingredient) {
        if (ingredient instanceof List<?> list) {
            for (Object object : list) {
                addIngredientObject(slotBuilder, object);
            }
        } else if (ingredient instanceof ItemStack itemStack) {
            slotBuilder.addItemStack(itemStack);
        } else if (ingredient instanceof SlimefunItemStack slimefunItemStack) {
            slotBuilder.addIngredient(JeiIntegration.SLIMEFUN, slimefunItemStack);
        } else if (ingredient instanceof JeiFluidIngredient fluidStack) {
            slotBuilder.addFluidStack(fluidStack.getFluid(), fluidStack.getAmount());
        }
    }

    @Override
    public Object fromTag(TagKey<Item> tagKey, int amount, Object defaultValue) {
        Optional<RegistryEntryList.Named<Item>> optional = Registries.ITEM.getEntryList(tagKey);
        if (optional.isEmpty()) {
            return defaultValue;
        }

        return optional.get().stream().map(ItemStack::new).toList();
    }

    @Override
    public Object fromItemStack(ItemStack itemStack, int amount, Object defaultValue) {
        itemStack.setCount(amount);
        return itemStack;
    }

    @Override
    public Object fromSlimefunItemStack(SlimefunItemStack slimefunItemStack, int amount, Object defaultValue) {
        return slimefunItemStack.setAmount(amount);
    }

    @Override
    public Object fromFluid(Fluid fluid, int amount, Object defaultValue) {
        return new JeiFluidIngredient(fluid, amount);
    }

    @Override
    public Object fromEntityType(EntityType<?> entityType, int amount, Object defaultValue) {
        // TODO: add support for entities
        return defaultValue;
    }
}

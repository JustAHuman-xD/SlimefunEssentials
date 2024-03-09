package me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers;

import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.NotNull;

public class SlimefunStackType implements IIngredientType<SlimefunItemStack> {
    @Override
    @NotNull
    public Class<? extends SlimefunItemStack> getIngredientClass() {
        return SlimefunItemStack.class;
    }
}

package me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers;

import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.compat.jei.JeiIntegration;
import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimefunStackHelper implements IIngredientHelper<SlimefunItemStack> {
    @Override
    @NotNull
    public IIngredientType<SlimefunItemStack> getIngredientType() {
        return JeiIntegration.SLIMEFUN;
    }
    
    @Override
    @NotNull
    public String getDisplayName(SlimefunItemStack ingredient) {
        return ingredient.itemStack().getName().getString();
    }
    
    @Override
    @NotNull
    public String getUniqueId(SlimefunItemStack ingredient, UidContext context) {
        return ingredient.id();
    }
    
    @Override
    @NotNull
    public Identifier getResourceLocation(SlimefunItemStack ingredient) {
        return Utils.newIdentifier(ingredient.id().toLowerCase());
    }
    
    @Override
    @NotNull
    public SlimefunItemStack copyIngredient(SlimefunItemStack ingredient) {
        return ingredient.copy();
    }
    
    @Override
    @NotNull
    public String getErrorInfo(@Nullable SlimefunItemStack ingredient) {
        return ingredient == null ? "SlimefunItemStack Ingredient is null!" : "Slimefun ItemStack \"" + ingredient.id() + "\"\nItemStack: " + ingredient.itemStack().toString();
    }
}

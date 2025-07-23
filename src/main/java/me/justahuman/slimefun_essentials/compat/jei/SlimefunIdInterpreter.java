package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlimefunIdInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    private final IIngredientSubtypeInterpreter<ItemStack> defaultInterpreter;

    public SlimefunIdInterpreter(IIngredientSubtypeInterpreter<ItemStack> defaultInterpreter) {
        this.defaultInterpreter = defaultInterpreter;
    }

    @Override
    public @NotNull String apply(ItemStack ingredient, UidContext context) {
        final String sfId = Utils.getSlimefunId(ingredient);
        if (sfId == null) {
            return this.defaultInterpreter == null ? IIngredientSubtypeInterpreter.NONE : this.defaultInterpreter.apply(ingredient, context);
        }
        return sfId;
    }
}

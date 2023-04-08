package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.api.IdInterpreter;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class ReiRecipeInterpreter implements IdInterpreter<EntryIngredient> {
    public List<EntryIngredient> getInputEntries(SlimefunRecipe slimefunRecipe) {
        final List<EntryIngredient> ingredients = new ArrayList<>();
        for (SlimefunRecipeComponent component : slimefunRecipe.inputs()) {
            ingredients.add(ReiIntegration.RECIPE_INTERPRETER.entryIngredientFromComponent(component));
        }
        return ingredients;
    }

    public List<EntryIngredient> getOutputEntries(SlimefunRecipe slimefunRecipe) {
        final List<EntryIngredient> ingredients = new ArrayList<>();
        for (SlimefunRecipeComponent component : slimefunRecipe.outputs()) {
            ingredients.add(ReiIntegration.RECIPE_INTERPRETER.entryIngredientFromComponent(component));
        }
        return ingredients;
    }

    public EntryIngredient entryIngredientFromComponent(SlimefunRecipeComponent component) {
        if (component.getMultiId() != null) {
            EntryIngredient.Builder builder = EntryIngredient.builder();
            for (String id : component.getMultiId()) {
                builder.addAll(interpretId(id, EntryIngredient.empty()));
            }
            return builder.build();
        } else {
            return interpretId(component.getId(), EntryIngredient.empty());
        }
    }

    @Override
    public EntryIngredient fromTag(TagKey<Item> tagKey, int amount, EntryIngredient defaultValue) {
        return EntryIngredients.ofItemTag(tagKey);
    }

    @Override
    public EntryIngredient fromItemStack(ItemStack itemStack, int amount, EntryIngredient defaultValue) {
        itemStack.setCount(amount);
        return EntryIngredients.of(itemStack);
    }

    @Override
    public EntryIngredient fromSlimefunItemStack(SlimefunItemStack slimefunItemStack, int amount, EntryIngredient defaultValue) {
        return EntryIngredient.of(EntryStack.of(ReiIntegration.SLIMEFUN, slimefunItemStack.setAmount(amount)));
    }

    @Override
    public EntryIngredient fromFluid(Fluid fluid, int amount, EntryIngredient defaultValue) {
        return EntryIngredients.of(fluid, amount);
    }

    @Override
    public EntryIngredient fromEntityType(EntityType<?> entityType, int amount, EntryIngredient defaultValue) {
        // TODO: add entity support
        return defaultValue;
    }
}

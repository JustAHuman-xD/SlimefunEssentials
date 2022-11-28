package me.justahuman.slimefuntoemi.recipehandler;

import dev.emi.emi.api.EmiRecipeHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class MultiblockHandler implements EmiRecipeHandler<Generic3x3ContainerScreenHandler> {

    @Override
    public List<Slot> getInputSources(Generic3x3ContainerScreenHandler handler) {
        final List<Slot> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(handler.getSlot(i));
        }
        int invStart = 10;
        for (int i = invStart; i < invStart + 35; i++) {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getCraftingSlots(Generic3x3ContainerScreenHandler handler) {
        List<Slot> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().getId().toString().contains("multiblock");
    }
}

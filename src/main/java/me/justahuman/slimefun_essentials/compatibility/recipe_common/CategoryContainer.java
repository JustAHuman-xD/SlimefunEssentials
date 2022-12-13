package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class CategoryContainer {

    @Getter
    private final Identifier id;

    @Getter
    private final ItemStack workstation;

    @Getter
    @Setter
    private List<RecipeContainer> recipes;

    public CategoryContainer(Identifier id, ItemStack workstation, List<RecipeContainer> recipes) {
        this.id = id;
        this.workstation = workstation;
        this.recipes = recipes;
    }
}

package me.justahuman.slimefuntoemi;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Category extends EmiRecipeCategory {
    private final Text machineName;

    public Category(Identifier id, EmiRenderable icon, Text machineName) {

        super(id, icon);
        this.machineName = machineName;
    }

    @Override
    public Text getName() {
        return Text.translatable(EmiUtil.translateId("emi.category.", getId()), machineName);
    }
}

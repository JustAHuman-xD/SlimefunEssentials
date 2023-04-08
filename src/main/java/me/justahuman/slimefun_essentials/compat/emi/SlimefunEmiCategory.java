package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimefunEmiCategory extends EmiRecipeCategory {
    private final Text displayName;
    
    public SlimefunEmiCategory(EmiRegistry emiRegistry, Identifier id, EmiStack workstation) {
        super(id, workstation);
        
        this.displayName = workstation.getItemStack().getName();
        emiRegistry.addWorkstation(this, workstation);
    }
    
    @Override
    public Text getName() {
        return Text.translatable("emi.category.slimefun", this.displayName);
    }
}

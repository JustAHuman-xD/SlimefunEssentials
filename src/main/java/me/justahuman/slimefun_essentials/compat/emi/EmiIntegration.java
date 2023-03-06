package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class EmiIntegration implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        for (Map.Entry<String, ItemStack> entry : ResourceLoader.getSlimefunItems().entrySet()) {
            emiRegistry.setDefaultComparison(EmiStack.of(entry.getValue()), original -> original.copy().nbt(true).build());
        }
        
        // TODO All of the recipe stuff
    }
}

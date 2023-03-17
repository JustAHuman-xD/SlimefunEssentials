package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.api.IdInterpreter;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import me.justahuman.slimefun_essentials.compat.emi.recipes.AncientAltarRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.GridRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.ProcessRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.ReactorRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.SmelteryRecipe;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmiIntegration implements EmiPlugin, IdInterpreter<EmiIngredient> {
    private static final Map<String, SlimefunEmiCategory> slimefunCategories = new HashMap<>();
    
    @Override
    public void register(EmiRegistry emiRegistry) {
        for (Map.Entry<String, SlimefunItemStack> entry : ResourceLoader.getSlimefunItems().entrySet()) {
            emiRegistry.setDefaultComparison(EmiStack.of(entry.getValue().itemStack()), original -> original.copy().nbt(true).build());
        }
        
        for (SlimefunCategory slimefunCategory : SlimefunCategory.getSlimefunCategories().values()) {
            final String workstationId = slimefunCategory.id();
            final String type = slimefunCategory.type();
            final Integer speed = slimefunCategory.speed();
            final Identifier categoryIdentifier = Utils.newIdentifier(workstationId);
            final EmiStack workStation = emiStackFromId(workstationId + ":1");
            final SlimefunEmiCategory slimefunEmiCategory;
            if (slimefunCategories.containsKey(workstationId)) {
                slimefunEmiCategory = slimefunCategories.get(workstationId);
            } else {
                slimefunEmiCategory = new SlimefunEmiCategory(emiRegistry, categoryIdentifier, workStation);
                slimefunCategories.put(workstationId, slimefunEmiCategory);
                emiRegistry.addCategory(slimefunEmiCategory);
            }
            
            for (SlimefunRecipe slimefunRecipe : slimefunCategory.recipes()) {
                final List<EmiIngredient> inputs = new ArrayList<>();
                final List<EmiStack> outputs = new ArrayList<>();
                final List<SlimefunLabel> labels = slimefunRecipe.labels();
                final Integer time = slimefunRecipe.time();
                final Integer recipeEnergy = slimefunRecipe.energy();
    
                for (SlimefunRecipeComponent input : slimefunRecipe.inputs()) {
                    inputs.add(emiIngredientFromComponent(input));
                }
                
                for (SlimefunRecipeComponent output : slimefunRecipe.outputs()) {
                    outputs.add(emiStackFromComponent(output));
                }
                
                final EmiRecipe emiRecipe = getEmiRecipe(type, slimefunEmiCategory, inputs, outputs, labels, recipeEnergy, time, speed);
                emiRegistry.addRecipe(emiRecipe);
            }
        }
    }
    
    public static EmiRecipe getEmiRecipe(String type, EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs, List<SlimefunLabel> labels, Integer energy, Integer time, Integer speed) {
        if (type.equals("ancient_altar")) {
            return new AncientAltarRecipe(category, inputs, outputs, time);
        } else if (type.equals("smeltery")) {
            return new SmelteryRecipe(category, inputs, outputs, energy, time, speed);
        } else if (type.equals("reactor")) {
            return new ReactorRecipe(category, inputs, outputs, energy, time);
        } else if (type.contains("grid")) {
            return new GridRecipe(category, inputs, outputs, energy, time, speed, type);
        } else {
            return new ProcessRecipe(category, inputs, outputs, labels, energy, time, speed);
        }
    }
    
    /**
     * Returns an {@link EmiStack} based off of the provided id
     * @param id The {@link String} id to retrieve the {@link EmiStack} from
     * @return {@link EmiStack} represented by the id or {@link EmiStack#EMPTY} if the id is invalid
     */
    public EmiStack emiStackFromId(String id) {
        final EmiIngredient emiIngredient = interpretId(id, EmiStack.EMPTY);
        if (emiIngredient instanceof EmiStack emiStack) {
            return emiStack;
        }
        
        Utils.warn("Invalid EmiStack Id: " + id);
        return EmiStack.EMPTY;
    }
    
    public EmiStack emiStackFromComponent(SlimefunRecipeComponent component) {
        final EmiIngredient emiIngredient = emiIngredientFromComponent(component);
        if (emiIngredient instanceof EmiStack emiStack) {
            return emiStack;
        }
    
        Utils.warn("Invalid EmiStack Component: " + component);
        return EmiStack.EMPTY;
    }
    
    public EmiIngredient emiIngredientFromComponent(SlimefunRecipeComponent component) {
        final List<String> multiId = component.getMultiId();
        if (multiId != null) {
            final List<EmiIngredient> multiStack = new ArrayList<>();
            for (String id : multiId) {
                multiStack.add(interpretId(id, EmiStack.EMPTY));
            }
            return EmiIngredient.of(multiStack, multiStack.isEmpty() ? 1 : multiStack.get(0).getAmount());
        }
        
        return interpretId(component.getId(), EmiStack.EMPTY);
    }
    
    @Override
    public EmiIngredient fromTag(TagKey<Item> tagKey, int amount, EmiIngredient defaultValue) {
        return EmiIngredient.of(tagKey, amount);
    }
    
    @Override
    public EmiIngredient fromItemStack(ItemStack itemStack, int amount, EmiIngredient defaultValue) {
        return EmiStack.of(itemStack, amount);
    }
    
    @Override
    public EmiIngredient fromSlimefunItemStack(SlimefunItemStack slimefunItemStack, int amount, EmiIngredient defaultValue) {
        return EmiStack.of(slimefunItemStack.itemStack(), amount);
    }
    
    @Override
    public EmiIngredient fromFluid(Fluid fluid, int amount, EmiIngredient defaultValue) {
        return EmiStack.of(fluid);
    }
    
    @Override
    public EmiIngredient fromEntityType(EntityType<?> entityType, int amount, EmiIngredient defaultValue) {
        return new EntityEmiStack(entityType, amount);
    }
}

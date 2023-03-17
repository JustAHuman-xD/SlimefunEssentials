package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmiIntegration implements EmiPlugin {
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
    public static EmiStack emiStackFromId(String id) {
        final EmiIngredient emiIngredient = emiIngredientFromId(id);
        if (emiIngredient instanceof EmiStack emiStack) {
            return emiStack;
        }
        
        Utils.warn("Invalid EmiStack Id: " + id);
        return EmiStack.EMPTY;
    }
    
    /**
     * Returns an {@link EmiIngredient} based off of the provided id
     * @param id The {@link String} id to retrieve the {@link EmiIngredient} from
     * @return {@link EmiIngredient} represented by the id or {@link EmiStack#EMPTY} if the id is invalid
     */
    public static EmiIngredient emiIngredientFromId(String id) {
        if (id.equals("")) {
            return EmiStack.EMPTY;
        }
        
        if (!id.contains(":")) {
            Utils.warn("Invalid EmiIngredient Id: " + id);
            return EmiStack.EMPTY;
        }
        
        final String type = id.substring(0, id.indexOf(":"));
        final String value = id.substring(id.indexOf(":") + 1);
        int amount;
        try {
            amount = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            amount = 1;
        }
        
        if (ResourceLoader.getSlimefunItems().containsKey(type)) {
            return EmiStack.of(ResourceLoader.getSlimefunItems().get(type).itemStack()).copy().setAmount(amount);
        }
        
        if (type.equals("entity")) {
            final Identifier identifier = new Identifier("minecraft:" + value);
            if (!Registries.ENTITY_TYPE.containsId(identifier)) {
               Utils.warn("Invalid EmiIngredient Entity Id: " + id);
               return EmiStack.EMPTY;
            }
            return new EntityEmiStack(Registries.ENTITY_TYPE.get(identifier));
        } else if (type.equals("fluid")) {
            final Identifier identifier = new Identifier("minecraft:" + value);
            if (!Registries.FLUID.containsId(identifier)) {
                Utils.warn("Invalid EmiIngredient Fluid Id: " + id);
                return EmiStack.EMPTY;
            }
            return EmiStack.of(Registries.FLUID.get(identifier));
        } else if (type.contains("#")) {
            final Identifier identifier = new Identifier("minecraft:" + type.substring(1));
            return EmiIngredient.of(TagKey.of(Registries.ITEM.getKey(), identifier), amount);
        } else {
            final Identifier identifier = new Identifier("minecraft:" + type.toLowerCase());
            if (!Registries.ITEM.containsId(identifier)) {
                Utils.warn("Invalid EmiIngredient Item Id: " + id);
                return EmiStack.EMPTY;
            }
            return EmiStack.of(Registries.ITEM.get(identifier)).copy().setAmount(amount);
        }
    }
    
    public static EmiStack emiStackFromComponent(SlimefunRecipeComponent component) {
        final EmiIngredient emiIngredient = emiIngredientFromComponent(component);
        if (emiIngredient instanceof EmiStack emiStack) {
            return emiStack;
        }
    
        Utils.warn("Invalid EmiStack Component: " + component);
        return EmiStack.EMPTY;
    }
    
    public static EmiIngredient emiIngredientFromComponent(SlimefunRecipeComponent component) {
        final List<String> multiId = component.getMultiId();
        if (multiId != null) {
            final List<EmiIngredient> multiStack = new ArrayList<>();
            for (String id : multiId) {
                multiStack.add(emiIngredientFromId(id));
            }
            return EmiIngredient.of(multiStack, multiStack.isEmpty() ? 1 : multiStack.get(0).getAmount());
        }
        
        final String id = component.getId();
        return emiIngredientFromId(id);
    }
}

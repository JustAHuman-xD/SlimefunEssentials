package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeComponent;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ProcessDisplay implements Display {
    protected final SlimefunCategory slimefunCategory;
    protected final SlimefunRecipe slimefunRecipe;
    
    public ProcessDisplay(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        this.slimefunCategory = slimefunCategory;
        this.slimefunRecipe = slimefunRecipe;
    }
    
    public int getDisplayWidth() {
        return TextureUtils.getProcessWidth(slimefunRecipe.labels(), slimefunRecipe.inputs(), slimefunRecipe.outputs(), slimefunRecipe.energy());
    }
    
    @Override
    public List<EntryIngredient> getInputEntries() {
        final List<EntryIngredient> ingredients = new ArrayList<>();
        for (SlimefunRecipeComponent component : slimefunRecipe.inputs()) {
            ingredients.add(entryIngredientFromComponent(component));
        }
        return ingredients;
    }
    
    @Override
    public List<EntryIngredient> getOutputEntries() {
        final List<EntryIngredient> ingredients = new ArrayList<>();
        for (SlimefunRecipeComponent component : slimefunRecipe.outputs()) {
            ingredients.add(entryIngredientFromComponent(component));
        }
        return ingredients;
    }
    
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.newIdentifier(slimefunCategory.id().toLowerCase()));
    }
    
    public EntryIngredient entryIngredientFromComponent(SlimefunRecipeComponent component) {
        if (component.getMultiId() != null) {
            EntryIngredient.Builder builder = EntryIngredient.builder();
            for (String id : component.getMultiId()) {
                builder.addAll(entryIngredientFromId(id));
            }
            return builder.build();
        } else {
            return entryIngredientFromId(component.getId());
        }
    }
    
    public EntryIngredient entryIngredientFromId(String id) {
        if (id.equals("")) {
            return EntryIngredient.empty();
        }
    
        if (!id.contains(":")) {
            Utils.warn("Invalid ReiIngredient Id: " + id);
            return EntryIngredient.empty();
        }
    
        final String type = id.substring(0, id.indexOf(":"));
        final String value = id.substring(id.indexOf(":") + 1);
        int amount;
        try {
            amount = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            amount = 1;
        }
        
        // TODO: Properly set amounts
    
        if (ResourceLoader.getSlimefunItems().containsKey(type)) {
            return EntryIngredient.empty();
        }
    
        if (type.equals("entity")) {
            final Identifier identifier = new Identifier("minecraft:" + value);
            if (! Registries.ENTITY_TYPE.containsId(identifier)) {
                Utils.warn("Invalid ReiIngredient Entity Id: " + id);
                return EntryIngredient.empty();
            }
            // TODO: Add entity support
            return EntryIngredient.empty();
        } else if (type.equals("fluid")) {
            final Identifier identifier = new Identifier("minecraft:" + value);
            if (!Registries.FLUID.containsId(identifier)) {
                Utils.warn("Invalid ReiIngredient Fluid Id: " + id);
                return EntryIngredient.empty();
            }
            return EntryIngredients.of(Registries.FLUID.get(identifier));
        } else if (type.contains("#")) {
            final Identifier identifier = new Identifier("minecraft:" + type.substring(1));
            return EntryIngredients.ofItemTag(TagKey.of(Registries.ITEM.getKey(), identifier));
        } else {
            final Identifier identifier = new Identifier("minecraft:" + type.toLowerCase());
            if (!Registries.ITEM.containsId(identifier)) {
                Utils.warn("Invalid ReiIngredient Item Id: " + id);
                return EntryIngredient.empty();
            }
            return EntryIngredients.of(Registries.ITEM.get(identifier));
        }
    }
}

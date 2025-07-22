package me.justahuman.slimefun_essentials.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.utils.DataUtils;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlimefunRecipe {
    protected RecipeCategory parent;
    protected Integer sfTicks;
    protected Integer ticks;
    protected Integer energy;
    protected List<RecipeComponent> inputs;
    protected List<RecipeComponent> outputs;
    protected List<String> labels;

    public SlimefunRecipe(RecipeCategory parent, Integer sfTicks, Integer ticks, Integer energy, List<RecipeComponent> inputs, List<RecipeComponent> outputs, List<String> labels) {
        this.parent = parent;
        this.sfTicks = sfTicks;
        this.ticks = ticks;
        this.energy = energy;
        this.inputs = inputs;
        this.outputs = outputs;
        this.labels = labels;
    }

    public static SlimefunRecipe deserialize(RecipeCategory parent, ByteArrayDataInput input, Integer workstationEnergy) {
        final Integer sfTicks = DataUtils.get(input, (Integer) null);
        final Integer ticks = DataUtils.get(input, (Integer) null);
        final Integer energy = DataUtils.get(input, workstationEnergy);
        final List<RecipeComponent> inputs = new ArrayList<>();
        final List<RecipeComponent> outputs = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        final List<ItemStack> complex = new ArrayList<>();

        int complexSize = input.readInt();
        for (int i = 0; i < complexSize; i++) {
            complex.add(DataUtils.get(input));
        }

        int inputSize = input.readInt();
        for (int i = 0; i < inputSize; i++) {
            inputs.add(RecipeComponent.deserialize(complex, input));
        }

        int outputSize = input.readInt();
        for (int i = 0; i < outputSize; i++) {
            outputs.add(RecipeComponent.deserialize(complex, input));
        }

        int labelSize = input.readInt();
        for (int i = 0; i < labelSize; i++) {
            String type = input.readUTF();
            if (DisplayComponentType.get(type) != null) {
                labels.add(type);
            }
        }

        return new SlimefunRecipe(parent, sfTicks, ticks, energy, inputs, outputs, labels);
    }

    public static SlimefunRecipe deserialize(RecipeCategory parent, JsonObject recipeObject, Integer workstationEnergy) {
        final Integer sfTicks = JsonUtils.get(recipeObject, "sf_ticks", (Integer) null);
        final Integer ticks = JsonUtils.get(recipeObject, "ticks", (Integer) null);
        final Integer energy = JsonUtils.get(recipeObject, "energy", workstationEnergy);
        final List<RecipeComponent> inputs = new ArrayList<>();
        final List<RecipeComponent> outputs = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        final List<ItemStack> complex = new ArrayList<>();

        for (JsonElement complexElement : JsonUtils.get(recipeObject, "complex", new JsonArray())) {
            if (complexElement instanceof JsonObject object) {
                complex.add(JsonUtils.deserializeItem(object));
            }
        }

        for (JsonElement inputElement : JsonUtils.get(recipeObject, "inputs", new JsonArray())) {
            final RecipeComponent inputRecipeElement = RecipeComponent.deserialize(complex, inputElement);
            if (inputRecipeElement != null) {
                inputs.add(inputRecipeElement);
            }
        }

        for (JsonElement outputElement : JsonUtils.get(recipeObject, "outputs", new JsonArray())) {
            final RecipeComponent outputRecipeElement = RecipeComponent.deserialize(complex, outputElement);
            if (outputRecipeElement != null) {
                outputs.add(outputRecipeElement);
            }
        }

        for (JsonElement labelElement : JsonUtils.get(recipeObject, "labels", new JsonArray())) {
            if (!(labelElement instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
                continue;
            }

            String type = jsonPrimitive.getAsString();
            if (DisplayComponentType.get(type) != null) {
                labels.add(type);
            }
        }

        return new SlimefunRecipe(parent, sfTicks, ticks, energy, inputs, outputs, labels);
    }

    public boolean hasLabels() {
        return this.labels != null && !this.labels.isEmpty();
    }

    public boolean hasEnergy() {
        return this.energy != null && this.energy != 0;
    }

    public boolean hasInputs() {
        return this.inputs != null && !this.inputs.isEmpty();
    }

    public boolean hasTime() {
        return this.sfTicks != null || this.ticks != null;
    }

    public boolean hasOutputs() {
        return this.outputs != null && !this.outputs.isEmpty();
    }

    public RecipeCategory parent() {
        return this.parent;
    }

    public int sfTicks() {
        if (!hasTime()) {
            return 0;
        }
        return this.sfTicks != null
                ? this.sfTicks / this.parent.speed()
                : SlimefunRegistry.toSfTicks(this.ticks) / this.parent.speed();
    }

    public int ticks() {
        if (!hasTime()) {
            return 0;
        }
        return this.ticks != null
                ? this.ticks / this.parent.speed()
                : SlimefunRegistry.toTicks(this.sfTicks / this.parent.speed());
    }

    public double seconds() {
        return this.sfTicks != null
                ? (double) sfTicks() / SlimefunRegistry.getSfTicksPerSecond()
                : ticks() / 20.0;
    }

    public int millis() {
        return (int) (seconds() * 1000);
    }

    public Integer energy() {
        return this.energy;
    }

    public Integer totalEnergy() {
        if (!this.hasEnergy()) {
            return null;
        }
        return hasTime() ? this.energy * sfTicks() : this.energy;
    }

    public List<RecipeComponent> inputs() {
        return this.inputs;
    }

    public List<RecipeComponent> outputs() {
        return this.outputs;
    }

    public List<String> labels() {
        return this.labels;
    }

    public SlimefunRecipe copy(RecipeCategory newParent) {
        return new SlimefunRecipe(newParent, this.sfTicks, this.ticks, this.energy, new ArrayList<>(this.inputs), new ArrayList<>(this.outputs), new ArrayList<>(this.labels));
    }
}

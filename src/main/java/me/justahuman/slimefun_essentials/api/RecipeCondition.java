package me.justahuman.slimefun_essentials.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.DataUtils;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface RecipeCondition {
    RecipeCondition TRUE = recipe -> true;
    RecipeCondition FALSE = recipe -> false;

    boolean passes(SlimefunRecipe recipe);

    static RecipeCondition deserialize(ByteArrayDataInput input) {
        if (input.readBoolean()) {
            String property = DataUtils.get(input, "0.0");
            String comparator = DataUtils.get(input, "");
            String value = DataUtils.get(input, "0.0");
            Function<SlimefunRecipe, Number> propertyFunction = recipe -> Utils.resolveNumberPlaceholder(property, recipe, null);
            Function<SlimefunRecipe, Number> valueFunction = recipe -> Utils.resolveNumberPlaceholder(value, recipe, null);
            return deserialize(propertyFunction, comparator, valueFunction);
        } else {
            return input.readBoolean() ? TRUE : FALSE;
        }
    }

    static RecipeCondition deserialize(JsonElement condition) {
        if (condition instanceof JsonObject object) {
            Function<SlimefunRecipe, Number> property = recipe -> Utils.resolveNumberPlaceholder(JsonUtils.get(object, "property", "0.0"), recipe, null);
            String comparator = JsonUtils.get(object, "condition", "");
            Function<SlimefunRecipe, Number> value = recipe -> Utils.resolveNumberPlaceholder(JsonUtils.get(object, "value", "0.0"), recipe, null);
            return deserialize(property, comparator, value);
        } else {
            return !(condition instanceof JsonPrimitive primitive) || !primitive.isBoolean() || primitive.getAsBoolean() ? TRUE : FALSE;
        }
    }

    static RecipeCondition deserialize(Function<SlimefunRecipe, Number> property, String comparator, Function<SlimefunRecipe, Number> value) {
        BiFunction<Number, Number, Boolean> comparison = switch (comparator) {
            case ">" -> (a, b) -> a != null && a.doubleValue() > b.doubleValue();
            case ">=" -> (a, b) -> a != null && a.doubleValue() >= b.doubleValue();
            case "<" -> (a, b) -> a != null && a.doubleValue() < b.doubleValue();
            case "<=" -> (a, b) -> a != null && a.doubleValue() <= b.doubleValue();
            case "=" -> (a, b) -> a != null && a.doubleValue() == b.doubleValue();
            case "!=" -> (a, b) -> a != null && a.doubleValue() != b.doubleValue();
            case "?" -> (a, b) -> a != null;
            default -> (a, b) -> false;
        };
        return recipe -> comparison.apply(property.apply(recipe), value.apply(recipe));
    }
}

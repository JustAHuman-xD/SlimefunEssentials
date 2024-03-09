package me.justahuman.slimefun_essentials.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class JsonUtils {
    public static JsonObject getObjectOrDefault(JsonObject jsonObject, String key, JsonObject defaultValue) {
        return jsonObject.get(key) instanceof JsonObject otherObject ? otherObject : defaultValue;
    }
    
    public static JsonArray getArrayOrDefault(JsonObject jsonObject, String key, JsonArray defaultValue, boolean shouldSet) {
        final JsonArray result = getArrayOrDefault(jsonObject, key, defaultValue);
        if (shouldSet) {
            jsonObject.add(key, result);
        }
        return result;
    }
    
    public static JsonArray getArrayOrDefault(JsonObject jsonObject, String key, JsonArray defaultValue) {
        return jsonObject.get(key) instanceof JsonArray jsonArray ? jsonArray : defaultValue;
    }
    
    public static String getStringOrDefault(JsonObject jsonObject, String key, String defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString() ? jsonPrimitive.getAsString() : defaultValue;
    }
    
    public static Boolean getBooleanOrDefault(JsonObject jsonObject, String key, Boolean defaultValue, boolean shouldSet) {
        final Boolean result = getBooleanOrDefault(jsonObject, key, defaultValue);
        if (shouldSet) {
            jsonObject.addProperty(key, result);
        }
        return result;
    }
    
    public static Boolean getBooleanOrDefault(JsonObject jsonObject, String key, Boolean defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isBoolean() ? jsonPrimitive.getAsBoolean() : defaultValue;
    }
    
    public static Long getLongOrDefault(JsonObject jsonObject, String key, Long defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isNumber() ? jsonPrimitive.getAsLong() : defaultValue;
    }
    
    public static Integer getIntegerOrDefault(JsonObject jsonObject, String key, Integer defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isNumber() ? jsonPrimitive.getAsInt() : defaultValue;
    }
    
    public static ItemStack deserializeItem(JsonObject itemObject) {
        final ItemStack itemStack = new ItemStack(Registries.ITEM.get(new Identifier(itemObject.get("item").getAsString())));
        itemStack.setCount(JsonHelper.getInt(itemObject, "amount", 1));
        if (JsonHelper.hasString(itemObject, "nbt")) {
            itemStack.setNbt(parseNbt(itemObject));
        }
        
        return itemStack;
    }
    
    public static NbtCompound parseNbt(JsonObject json) {
        return parseNbt(JsonHelper.getString(json, "nbt"));
    }
    
    public static NbtCompound parseNbt(String nbt) {
        try {
            return StringNbtReader.parse(nbt);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

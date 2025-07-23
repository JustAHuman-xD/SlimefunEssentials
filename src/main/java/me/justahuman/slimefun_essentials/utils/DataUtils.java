package me.justahuman.slimefun_essentials.utils;

import com.google.common.io.ByteArrayDataInput;
import com.mojang.serialization.DynamicOps;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {
    public static ItemStack getStack(ByteArrayDataInput input, int dataVersion) {
        final ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.tryParse(input.readUTF())));
        itemStack.setCount(input.readInt());

        try {
            if (input.readBoolean() && itemStack.getComponents() instanceof ComponentMapImpl components) {
                components.setChanges(ComponentChanges.CODEC.decode(withRegistryAccess(NbtOps.INSTANCE), StringNbtReader.parse(input.readUTF())).getOrThrow().getFirst());
            }
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Failed to deserialize item components", e);
        }
        return itemStack;
    }

    public static ItemStack getStack(ByteArrayDataInput input, ItemStack def, int dataVersion) {
        if (!input.readBoolean()) {
            return def;
        }
        return getStack(input, dataVersion);
    }

    public static String get(ByteArrayDataInput input, String def) {
        return input.readBoolean() ? input.readUTF() : def;
    }

    public static Boolean get(ByteArrayDataInput input, Boolean def) {
        return input.readBoolean() ? input.readBoolean() : def;
    }

    public static Long get(ByteArrayDataInput input, Long def) {
        return input.readBoolean() ? Long.valueOf(input.readLong()) : def;
    }

    public static Integer get(ByteArrayDataInput input, Integer def) {
        return input.readBoolean() ? Integer.valueOf(input.readInt()) : def;
    }

    public static List<TooltipComponent> getTooltip(ByteArrayDataInput input) {
        int size = input.readInt();
        if (size <= 0) {
            return List.of();
        }

        final List<TooltipComponent> tooltip = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            tooltip.add(TooltipComponent.of(Text.literal(input.readUTF()).asOrderedText()));
        }
        return tooltip;
    }

    private static <T> DynamicOps<T> withRegistryAccess(DynamicOps<T> ops) {
        MinecraftClient instance = MinecraftClient.getInstance();
        if (instance == null || instance.world == null) {
            return ops;
        }
        return instance.world.getRegistryManager().getOps(ops);
    }
}

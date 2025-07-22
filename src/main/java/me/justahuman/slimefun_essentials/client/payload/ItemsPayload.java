package me.justahuman.slimefun_essentials.client.payload;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.utils.DataUtils;
import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.LinkedHashMap;
import java.util.Map;

public record ItemsPayload(Map<String, ItemStack> items) implements CustomPayload {
    public static final ItemsPayload EMPTY = new ItemsPayload(null);
    public static final PacketCodec<PacketByteBuf, ItemsPayload> CODEC =
            Payloads.newSplitCodec(input -> {
                int size = input.readInt();
                Map<String, ItemStack> items = new LinkedHashMap<>(size);
                for (int i = 0; i < size; i++) {
                    String id = input.readUTF();
                    try {
                        items.put(id, DataUtils.get(input));
                    } catch (Exception e) {
                        SlimefunEssentials.LOGGER.error("Failed to deserialize slimefun item: {}", id, e);
                    }
                }
                return new ItemsPayload(items);
            }, EMPTY);

    public void load() {
        SlimefunRegistry.addItems(items);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.ITEM_CHANNEL;
    }
}

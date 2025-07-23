package me.justahuman.slimefun_essentials.client.payload;

import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ComponentTypePayload implements CustomPayload {
    private static final ComponentTypePayload EMPTY = new ComponentTypePayload();
    public static final PacketCodec<PacketByteBuf, ComponentTypePayload> CODEC =
            Payloads.newSplitCodec(input -> {
                int dataVersion = input.readInt();
                DisplayComponentType.deserialize(input, dataVersion);
                Payloads.checkMetExpected();
                return EMPTY;
            }, EMPTY);

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.COMPONENT_TYPE_CHANNEL;
    }
}

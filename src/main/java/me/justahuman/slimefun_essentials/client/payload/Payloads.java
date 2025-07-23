package me.justahuman.slimefun_essentials.client.payload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Payloads {
    public static final String PLUGIN_ID = "slimefun_server_essentials";
    public static final CustomPayload.Id<ComponentTypePayload> COMPONENT_TYPE_CHANNEL = newChannel("component_types");
    public static final CustomPayload.Id<ItemsPayload> ITEM_CHANNEL = newChannel("items");
    public static final CustomPayload.Id<RecipeDisplayPayload> RECIPE_DISPLAY_CHANNEL = newChannel("recipe_displays");
    public static final CustomPayload.Id<RecipeCategoriesPayload> RECIPE_CATEGORIES_CHANNEL = newChannel("recipe_categories");
    public static final CustomPayload.Id<LoadingStatePayload> LOADING_STATE_CHANNEL = newChannel("loading_state");

    private static final Type RECEIVING_TOAST_TYPE = new Type("receiving_expected_packets");
    private static final Type RECEIVED_TOAST_TYPE = new Type("received_expected_packets");

    private static final int MAX_MESSAGE_SIZE = 32766;
    private static final int SPLIT_MESSAGE_SIZE = MAX_MESSAGE_SIZE - 4 - 4 - 4;
    private static final Map<Class<?>, Integer> PACKET_COUNTS = new HashMap<>();
    private static final Map<Class<?>, Integer> EXPECTED_PACKETS = new HashMap<>();
    private static Runnable onMeetExpected = showToast(RECEIVED_TOAST_TYPE);
    private static boolean metExpected = false;

    private static Runnable showToast(Type type) {
        return () -> {
            Text title = Text.translatable("slimefun_essentials.toast." + type.type);
            Text description = Text.translatable("slimefun_essentials.toast." + type.type + ".description",
                    PACKET_COUNTS.getOrDefault(ComponentTypePayload.class, 0),
                    EXPECTED_PACKETS.getOrDefault(ComponentTypePayload.class, 0),
                    PACKET_COUNTS.getOrDefault(ItemsPayload.class, 0),
                    EXPECTED_PACKETS.getOrDefault(ItemsPayload.class, 0),
                    PACKET_COUNTS.getOrDefault(RecipeCategoriesPayload.class, 0),
                    EXPECTED_PACKETS.getOrDefault(RecipeCategoriesPayload.class, 0),
                    PACKET_COUNTS.getOrDefault(RecipeDisplayPayload.class, 0),
                    EXPECTED_PACKETS.getOrDefault(RecipeDisplayPayload.class, 0));
            ToastManager manager = MinecraftClient.getInstance().getToastManager();
            SystemToast existing = manager.getToast(SystemToast.class, type);
            if (existing != null) {
                existing.setContent(title, description);
            } else {
                manager.add(new SystemToast(type, title, description));
            }
        };
    }

    public static void onMeetExpected(Runnable runnable) {
        Runnable previous = onMeetExpected;
        onMeetExpected = () -> {
            previous.run();
            runnable.run();
        };
    }

    public static boolean isExpecting() {
        return !EXPECTED_PACKETS.isEmpty() && !metExpected;
    }

    public static boolean metExpected() {
        return metExpected;
    }

    public static void reset() {
        PACKET_COUNTS.clear();
        EXPECTED_PACKETS.clear();
        onMeetExpected = showToast(RECEIVED_TOAST_TYPE);
        metExpected = false;
    }

    public static void expect(LoadingStatePayload payload) {
        EXPECTED_PACKETS.clear();
        EXPECTED_PACKETS.put(ComponentTypePayload.class, payload.typePackets());
        EXPECTED_PACKETS.put(ItemsPayload.class, payload.itemPackets());
        EXPECTED_PACKETS.put(RecipeCategoriesPayload.class, payload.categoryPackets());
        EXPECTED_PACKETS.put(RecipeDisplayPayload.class, payload.displayPackets());
        SlimefunEssentials.LOGGER.info("Expecting {} type packets, {} item packets, {} category packets, and {} display packets",
                EXPECTED_PACKETS.get(ComponentTypePayload.class),
                EXPECTED_PACKETS.get(ItemsPayload.class),
                EXPECTED_PACKETS.get(RecipeCategoriesPayload.class),
                EXPECTED_PACKETS.get(RecipeDisplayPayload.class));
        showToast(RECEIVING_TOAST_TYPE).run();
        checkMetExpected();
    }

    public static void checkMetExpected() {
        showToast(RECEIVING_TOAST_TYPE).run();
        for (Map.Entry<Class<?>, Integer> expected : EXPECTED_PACKETS.entrySet()) {
            if (PACKET_COUNTS.getOrDefault(expected.getKey(), 0) < expected.getValue()) {
                return;
            } else if (PACKET_COUNTS.getOrDefault(expected.getKey(), 0) > expected.getValue()) {
                SlimefunEssentials.LOGGER.warn("Received more packets than expected for {}", expected.getKey().getSimpleName());
            }
        }

        metExpected = !EXPECTED_PACKETS.isEmpty();
        if (metExpected) {
            SlimefunEssentials.LOGGER.info("Received every expected packet");
            RecipeCategory.finalizeCategories();
            onMeetExpected.run();
            onMeetExpected = showToast(RECEIVED_TOAST_TYPE);
        }
    }

    public static <P extends CustomPayload> CustomPayload.Id<P> newChannel(String channel) {
        return new CustomPayload.Id<>(Identifier.of(PLUGIN_ID, channel));
    }

    public static <P extends CustomPayload>PacketCodec<PacketByteBuf, P> newSplitCodec(Function<ByteArrayDataInput, P> decoder, P empty) {
        return new PacketCodec<>() {
            private final Map<Integer, byte[][]> received = new HashMap<>();

            @Override
            public void encode(PacketByteBuf buf, P value) {}

            @Override
            public P decode(PacketByteBuf buf) {
                PACKET_COUNTS.compute(empty.getClass(), (k, v) -> v == null ? 1 : v + 1);

                byte[] bytes = new byte[buf.readableBytes()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = buf.readByte();
                }

                int id = bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
                int pieces = bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7];
                int piece = bytes[8] << 24 | bytes[9] << 16 | bytes[10] << 8 | bytes[11];
                byte[] pieceBytes = new byte[bytes.length - 12];
                System.arraycopy(bytes, 12, pieceBytes, 0, pieceBytes.length);
                byte[][] piecesBytes = received.computeIfAbsent(id, k -> new byte[pieces][]);
                piecesBytes[piece] = pieceBytes;

                boolean complete = true;
                for (byte[] totalMessagePiece : piecesBytes) {
                    if (totalMessagePiece == null) {
                        complete = false;
                        break;
                    }
                }

                if (complete) {
                    byte[] totalBytes = new byte[pieces * SPLIT_MESSAGE_SIZE];
                    for (int i = 0; i < pieces; i++) {
                        System.arraycopy(piecesBytes[i], 0, totalBytes, i * SPLIT_MESSAGE_SIZE, piecesBytes[i].length);
                    }
                    received.remove(id);
                    return decoder.apply(ByteStreams.newDataInput(totalBytes));
                }
                return empty;
            }
        };
    }

    public static <P extends CustomPayload>PacketCodec<PacketByteBuf, P> newCodec(Function<ByteArrayDataInput, P> decoder) {
        return PacketCodec.of((value, buf) -> {}, buf -> {
            byte[] bytes = new byte[buf.readableBytes()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = buf.readByte();
            }
            return decoder.apply(ByteStreams.newDataInput(bytes));
        });
    }

    private static class Type extends SystemToast.Type {
        final String type;

        public Type(String type) {
            this.type = type;
        }
    }
}

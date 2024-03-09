package me.justahuman.slimefun_essentials.utils;

import net.minecraft.util.Identifier;

public class Channels {
    public static final Identifier ADDON_CHANNEL = newChannel("addon");
    public static final Identifier BLOCK_CHANNEL = newChannel("block");

    public static Identifier newChannel(String channel) {
        return new Identifier("slimefun_server_essentials", channel);
    }
}

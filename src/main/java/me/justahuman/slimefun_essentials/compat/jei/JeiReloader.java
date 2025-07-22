package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.SlimefunEssentials;

import java.util.concurrent.atomic.AtomicReference;

public interface JeiReloader {
    AtomicReference<JeiReloader> instance = new AtomicReference<>();

    void slimefunEssentials$reloadJei();

    static void reload() {
        JeiReloader reloader = instance.get();
        if (reloader != null) {
            reloader.slimefunEssentials$reloadJei();
        } else {
            SlimefunEssentials.LOGGER.error("JeiReloader instance is not set. Cannot reload JEI.");
        }
    }
}

package me.justahuman.slimefun_essentials.compat.rei;

import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class ReiUtils {
    public static void fillEntries(List<EntryIngredient> list, int size) {
        if (list.size() >= size) {
            return;
        }

        for (int i = list.size(); i < size; i++) {
            list.add(EntryIngredient.empty());
        }
    }
}

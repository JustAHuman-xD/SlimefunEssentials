package me.justahuman.slimefun_essentials.compatibility.recipe_common;

import lombok.Getter;

import java.util.List;

public record CustomMultiStack(@Getter List<Object> stacks, @Getter long amount) {}

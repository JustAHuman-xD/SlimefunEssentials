package me.justahuman.slimefun_essentials.api;

import me.justahuman.slimefun_essentials.client.SlimefunCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public abstract class RecipeRenderer {
    private final Type type;

    protected RecipeRenderer(Type type) {
        this.type = type;
    }

    public int getContentsWidth(SlimefunCategory slimefunCategory) {
        return this.type.getContentsWidth(slimefunCategory);
    }

    public int getContentsWidth(SlimefunRecipe slimefunRecipe) {
        return this.type.getContentsWidth(slimefunRecipe);
    }

    public int getContentsHeight(SlimefunCategory slimefunCategory) {
        return this.type.getContentsHeight(slimefunCategory);
    }

    public int getContentsHeight(SlimefunRecipe slimefunRecipe) {
        return this.type.getContentsHeight(slimefunRecipe);
    }

    public int getDisplayWidth(SlimefunCategory slimefunCategory) {
        return getContentsWidth(slimefunCategory) + TextureUtils.padding * 2;
    }

    public int getDisplayWidth(SlimefunRecipe slimefunRecipe) {
        return getContentsWidth(slimefunRecipe) + TextureUtils.padding * 2;
    }

    public int getDisplayHeight(SlimefunCategory slimefunCategory) {
        return getContentsHeight(slimefunCategory) + TextureUtils.padding * 2;
    }

    public int getDisplayHeight(SlimefunRecipe slimefunRecipe) {
        return getContentsHeight(slimefunRecipe) + TextureUtils.padding * 2;
    }

    protected int calculateXOffset(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        return (getDisplayWidth(slimefunCategory) - getContentsWidth(slimefunRecipe)) / 2;
    }

    protected int calculateXOffset(SlimefunRecipe slimefunRecipe) {
        return (getDisplayWidth(slimefunRecipe) - getContentsWidth(slimefunRecipe)) / 2;
    }

    protected int calculateYOffset(SlimefunCategory slimefunCategory, SlimefunRecipe slimefunRecipe) {
        return (getDisplayHeight(slimefunCategory) - getContentsHeight(slimefunRecipe)) / 2;
    }

    protected int calculateYOffset(SlimefunRecipe slimefunRecipe, int height) {
        return (getDisplayHeight(slimefunRecipe) - height) / 2;
    }

    public abstract static class Type {
        public static final Type ANCIENT_ALTAR = new Type() {
            @Override
            public int getContentsWidth(SlimefunCategory slimefunCategory) {
                return 138;
            }

            @Override
            public int getContentsWidth(SlimefunRecipe slimefunRecipe) {
                return 138;
            }

            @Override
            public int getContentsHeight(SlimefunCategory slimefunCategory) {
                return 80;
            }

            @Override
            public int getContentsHeight(SlimefunRecipe slimefunRecipe) {
                return 80;
            }
        };

        public static final Type PROCESS = new Type() {
            @Override
            public int getContentsWidth(SlimefunCategory slimefunCategory) {
                return TextureUtils.getProcessWidth(slimefunCategory);
            }

            @Override
            public int getContentsWidth(SlimefunRecipe slimefunRecipe) {
                return TextureUtils.getProcessWidth(slimefunRecipe);
            }

            @Override
            public int getContentsHeight(SlimefunCategory slimefunCategory) {
                return TextureUtils.getProcessHeight(slimefunCategory);
            }

            @Override
            public int getContentsHeight(SlimefunRecipe slimefunRecipe) {
                return TextureUtils.getProcessHeight(slimefunRecipe);
            }
        };

        public static final Type REACTOR = new Type() {
            @Override
            public int getContentsWidth(SlimefunCategory slimefunCategory) {
                return TextureUtils.getReactorWidth(slimefunCategory);
            }

            @Override
            public int getContentsWidth(SlimefunRecipe slimefunRecipe) {
                return TextureUtils.getReactorWidth(slimefunRecipe);
            }

            @Override
            public int getContentsHeight(SlimefunCategory slimefunCategory) {
                return TextureUtils.getReactorHeight(slimefunCategory);
            }

            @Override
            public int getContentsHeight(SlimefunRecipe slimefunRecipe) {
                return TextureUtils.getReactorHeight(slimefunRecipe);
            }
        };

        public static final Type SMELTERY = new Type() {
            @Override
            public int getContentsWidth(SlimefunCategory slimefunCategory) {
                return TextureUtils.getSmelteryWidth(slimefunCategory);
            }

            @Override
            public int getContentsWidth(SlimefunRecipe slimefunRecipe) {
                return TextureUtils.getSmelteryWidth(slimefunRecipe);
            }

            @Override
            public int getContentsHeight(SlimefunCategory slimefunCategory) {
                return TextureUtils.slotSize * 3;
            }

            @Override
            public int getContentsHeight(SlimefunRecipe slimefunRecipe) {
                return TextureUtils.slotSize * 3;
            }
        };

        public static Type GRID(int side) {
            return new Type() {
                @Override
                public int getContentsWidth(SlimefunCategory slimefunCategory) {
                    return TextureUtils.getGridWidth(slimefunCategory, side);
                }

                @Override
                public int getContentsWidth(SlimefunRecipe slimefunRecipe) {
                    return TextureUtils.getGridWidth(slimefunRecipe, side);
                }

                @Override
                public int getContentsHeight(SlimefunCategory slimefunCategory) {
                    return TextureUtils.getGridHeight(side);
                }

                @Override
                public int getContentsHeight(SlimefunRecipe slimefunRecipe) {
                    return TextureUtils.getGridHeight(side);
                }
            };
        }

        public abstract int getContentsWidth(SlimefunCategory slimefunCategory);
        public abstract int getContentsWidth(SlimefunRecipe slimefunRecipe);
        public abstract int getContentsHeight(SlimefunCategory slimefunCategory);
        public abstract int getContentsHeight(SlimefunRecipe slimefunRecipe);
    }
}

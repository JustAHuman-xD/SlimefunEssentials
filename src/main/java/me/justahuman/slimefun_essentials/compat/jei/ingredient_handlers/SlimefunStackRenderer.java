package me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers;

import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.library.render.ItemStackRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimefunStackRenderer implements IIngredientRenderer<SlimefunItemStack> {
    private final ItemStackRenderer itemStackRenderer;

    public SlimefunStackRenderer() {
        itemStackRenderer = new ItemStackRenderer();
    }
    
    @Override
    public void render(MatrixStack poseStack, @Nullable SlimefunItemStack ingredient) {
        if (ingredient == null) {
            return;
        }
    
        itemStackRenderer.render(poseStack, ingredient.itemStack());
    }
    
    @Override
    @NotNull
    public List<Text> getTooltip(SlimefunItemStack ingredient, TooltipContext tooltipFlag) {
        return itemStackRenderer.getTooltip(ingredient.itemStack(), tooltipFlag);
    }
    
    @Override
    @NotNull
    public TextRenderer getFontRenderer(MinecraftClient minecraft, SlimefunItemStack ingredient) {
        return itemStackRenderer.getFontRenderer(minecraft, ingredient.itemStack());
    }
}

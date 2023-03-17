package me.justahuman.slimefun_essentials.compat.jei.ingredient_handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlimefunStackRenderer implements IIngredientRenderer<SlimefunItemStack> {
    private static final Logger LOGGER = LogManager.getLogger();
    
    @Override
    public void render(MatrixStack poseStack, @Nullable SlimefunItemStack ingredient) {
        if (ingredient == null) {
            return;
        }
    
        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.multiplyPositionMatrix(poseStack.peek().getPositionMatrix());
        RenderSystem.enableDepthTest();
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer font = getFontRenderer(minecraft, ingredient);
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        itemRenderer.renderInGui(ingredient.itemStack(), 0, 0);
        itemRenderer.renderGuiItemOverlay(font, ingredient.itemStack(), 0, 0);
        RenderSystem.disableBlend();
        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
    }
    
    @Override
    @NotNull
    public List<Text> getTooltip(SlimefunItemStack ingredient, TooltipContext tooltipFlag) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        PlayerEntity player = minecraft.player;
        try {
            return ingredient.itemStack().getTooltip(player, tooltipFlag);
        } catch (RuntimeException | LinkageError e) {
            String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient.itemStack());
            LOGGER.error("Failed to get tooltip: {}", itemStackInfo, e);
            List<Text> list = new ArrayList<>();
            MutableText crash = Text.translatable("jei.tooltip.error.crash");
            list.add(crash.formatted(Formatting.RED));
            return list;
        }
    }
    
    @Override
    @NotNull
    public TextRenderer getFontRenderer(MinecraftClient minecraft, SlimefunItemStack ingredient) {
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        return renderHelper.getFontRenderer(minecraft, ingredient.itemStack());
    }
    
    @Override
    public int getWidth() {
        return 16;
    }
    
    @Override
    public int getHeight() {
        return 16;
    }
}

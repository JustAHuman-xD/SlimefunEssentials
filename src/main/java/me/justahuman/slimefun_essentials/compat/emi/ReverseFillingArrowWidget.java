package me.justahuman.slimefun_essentials.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.AnimatedTextureWidget;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.DrawContext;

public class ReverseFillingArrowWidget extends AnimatedTextureWidget {
    public ReverseFillingArrowWidget(int x, int y, int time) {
        super(TextureUtils.WIDGETS, x, y, 24, 17, 68, 239, time, true, true, false);
    }
    
    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.drawTexture(this.texture, this.x, this.y, this.width, this.height, this.u, this.v - this.height, this.regionWidth, this.regionHeight, this.textureWidth, this.textureHeight);
        super.render(graphics, mouseX, mouseY, delta);
    }
}

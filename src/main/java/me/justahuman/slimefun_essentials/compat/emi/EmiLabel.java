package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.TextureWidget;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public class EmiLabel extends TextureWidget {
    public EmiLabel(String id, EmiTexture texture, int x, int y) {
        super(texture.texture, x, y, texture.width, texture.height, texture.u, texture.v, texture.regionWidth, texture.regionHeight, texture.textureWidth, texture.textureHeight);
        
        tooltip((mx, my) -> List.of(TooltipComponent.of(Text.translatable("slimefun_essentials.recipes.label." + id).asOrderedText())));
    }
    
    public EmiLabel(SlimefunLabel slimefunLabel, int x, int y) {
        this(slimefunLabel.id(), new EmiTexture(slimefunLabel.light(), slimefunLabel.u(), slimefunLabel.v(), TextureUtils.LABEL_SIZE, TextureUtils.LABEL_SIZE), x, y);
    }
}

package me.justahuman.slimefun_essentials.mixins.jec;

import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ILabel.Converter.class)
public class ILabelMixin {
    @Inject(at = @At("RETURN"), method = "from", cancellable = true)
    private static void from(Object o, CallbackInfoReturnable<ILabel> cir) {
        if (o instanceof SlimefunItemStack slimefunItemStack) {
            cir.setReturnValue(new LItemStack(slimefunItemStack.itemStack()));
        }
    }
}

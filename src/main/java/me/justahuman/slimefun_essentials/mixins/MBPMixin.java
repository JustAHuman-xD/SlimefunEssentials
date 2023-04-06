package me.justahuman.slimefun_essentials.mixins;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import mod.omoflop.mbp.MBPData;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MBPData.class)
public class MBPMixin {
    @Inject(at = @At("HEAD"), method = "meetsPredicate", cancellable = true)
    private static void isSlimefunBlock(BlockView world, BlockPos pos, BlockState state, Identifier renderContext, CallbackInfoReturnable<Optional<Identifier>> cir) {
        if (ResourceLoader.getPlacedBlocks().containsKey(pos) && ResourceLoader.getSlimefunBlocks().containsKey(ResourceLoader.getPlacedBlocks().get(pos))) {
            final Identifier identifier = new Identifier("minecraft:" + ResourceLoader.getPlacedBlocks().get(pos));
            cir.setReturnValue(Optional.of(identifier));
        }
    }
}

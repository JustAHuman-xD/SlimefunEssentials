package me.justahuman.slimefun_essentials.mixins.mbp;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import mod.omoflop.mbp.MBPData;
import mod.omoflop.mbp.common.ContextIdentifiers;
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
public class MBPDataMixin {
    @Inject(at = @At("HEAD"), method = "meetsPredicate", cancellable = true)
    private static void isSlimefunBlock(BlockView world, BlockPos pos, BlockState state, Identifier renderContext, CallbackInfoReturnable<Optional<Identifier>> cir) {
        if (!renderContext.equals(ContextIdentifiers.ITEM_HELD) && !renderContext.equals(ContextIdentifiers.ITEM)) {
            final String id = ResourceLoader.getPlacedBlocks().get(pos);
            if (id == null) {
                return;
            }

            final Identifier model = ResourceLoader.getBlockModels().get(id);
            if (model == null) {
                return;
            }

            cir.setReturnValue(Optional.of(model));
        }
    }
}

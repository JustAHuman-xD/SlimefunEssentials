package me.justahuman.slimefun_essentials.mixins;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.utils.Utils;
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
        if (!renderContext.equals(ContextIdentifiers.ITEM_HELD) && !renderContext.equals(ContextIdentifiers.ITEM) && ResourceLoader.getPlacedBlocks().containsKey(pos) && ResourceLoader.getSlimefunBlocks().contains(ResourceLoader.getPlacedBlocks().get(pos))) {
            cir.setReturnValue(Optional.of(Utils.newIdentifier("block/" + ResourceLoader.getPlacedBlocks().get(pos))));
        }
    }
}

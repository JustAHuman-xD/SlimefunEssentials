package me.justahuman.slimefun_essentials.mixins.mbp;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import mod.omoflop.mbp.client.MBPModelLoadingPlugin;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(MBPModelLoadingPlugin.ModelIdLoader.class)
public class MBPProviderMixin {
    @Inject(at = @At("RETURN"), method = "load", cancellable = true)
    public void addSlimefunModels(ResourceManager manager, Executor executor, CallbackInfoReturnable<CompletableFuture<HashSet<Identifier>>> cir) {
        ResourceLoader.loadItems(manager);
        ResourceLoader.loadBlockModels(manager);
        cir.setReturnValue(cir.getReturnValue().thenApply(value -> {
            value.addAll(ResourceLoader.getBlockModels().values());
            return value;
        }));
    }
}

package me.justahuman.slimefun_essentials.mixins;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.utils.Utils;
import mod.omoflop.mbp.client.MBPResourceProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MBPResourceProvider.class)
public class MBPProviderMixin {
    @Inject(at = @At("HEAD"), method = "provideExtraModels")
    public void addSlimefunModels(ResourceManager manager, Consumer<Identifier> out, CallbackInfo ci) {
        ResourceLoader.loadBlocks(manager);
        ResourceLoader.getSlimefunBlocks().forEach(string -> out.accept(Utils.newIdentifier("block/" + string)));
    }
}

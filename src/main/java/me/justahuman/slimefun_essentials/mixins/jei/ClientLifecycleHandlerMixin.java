package me.justahuman.slimefun_essentials.mixins.jei;

import me.justahuman.slimefun_essentials.compat.jei.JeiReloader;
import mezz.jei.fabric.startup.ClientLifecycleHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientLifecycleHandler.class, remap = false)
public abstract class ClientLifecycleHandlerMixin implements JeiReloader {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(CallbackInfo ci) {
        JeiReloader.instance.set(this);
    }

    @Override
    public void slimefunEssentials$reloadJei() {
        stopJei();
        startJei();
    }

    @Shadow protected abstract void stopJei();
    @Shadow protected abstract void startJei();
}

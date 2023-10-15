package me.justahuman.slimefun_essentials.compat.jade;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        if (ModConfig.blockFeatures()) {
            registration.addRayTraceCallback(((hitResult, accessor, originalAccessor) -> {
                if (!(accessor instanceof BlockAccessor blockAccessor)) {
                    return accessor;
                }

                final BlockPos blockPos = blockAccessor.getPosition();
                if (!ResourceLoader.isSlimefunItem(blockPos)) {
                    return accessor;
                }

                final String id = ResourceLoader.getPlacedId(blockPos).toUpperCase();
                final SlimefunItemStack slimefunItem = ResourceLoader.getSlimefunItem(id);
                if (slimefunItem == null) {
                    return accessor;
                }

                final ItemStack itemStack = slimefunItem.itemStack();
                return registration.blockAccessor().from(blockAccessor).fakeBlock(itemStack).build();
            }));
        }
    }
}

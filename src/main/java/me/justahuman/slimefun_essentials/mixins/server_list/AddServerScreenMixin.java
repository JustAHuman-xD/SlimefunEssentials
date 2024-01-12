package me.justahuman.slimefun_essentials.mixins.server_list;

import me.justahuman.slimefun_essentials.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AddServerScreen.class)
public abstract class AddServerScreenMixin extends Screen {
    @Shadow @Final private ServerInfo server;
    @Shadow private TextFieldWidget serverNameField;

    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void addWhitelistToggleButton(CallbackInfo ci) {
        final boolean initialEnabled = ModConfig.isServerEnabled(server.name);
        final String initialLangKey = "slimefun_essentials.server_menu.%s_whitelist".formatted(initialEnabled ? "remove" : "add");
        final ButtonWidget.Builder serverWhitelist = ButtonWidget.builder(Text.translatable(initialLangKey), button -> {
            final List<String> whitelist = ModConfig.getServerWhitelist();
            if (!whitelist.remove(server.name)) {
                whitelist.add(server.name);
            }

            final boolean enabled = whitelist.contains(server.name);
            final String langKey = "slimefun_essentials.server_menu.%s_whitelist".formatted(enabled ? "remove" : "add");
            button.setMessage(Text.translatable(langKey));
            button.setTooltip(Tooltip.of(Text.translatable(langKey + ".tooltip"))); //    Standard Starting X Position + Width of Name Field + 4px Padding
        }).tooltip(Tooltip.of(Text.translatable(initialLangKey + ".tooltip"))).dimensions(this.width / 2 - 100 + 200 + 4, 66, 60, 20);

        this.addDrawableChild(serverWhitelist.build());
    }

    @Inject(method = "addAndClose", at = @At("HEAD"))
    public void renameWhitelistEntry(CallbackInfo ci) {
        final List<String> enabledServers = ModConfig.getServerWhitelist();
        if (enabledServers.remove(server.name)) {
            enabledServers.add(serverNameField.getText());
        }
    }
}

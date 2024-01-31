package de.erdbeerbaerlp.dcintegration.neoforge.mixin;

import de.erdbeerbaerlp.dcintegration.neoforge.DiscordIntegrationMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin used to detect player timeouts
 */
@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1001)
public class MixinNetHandlerPlayServer {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(final Component textComponent, CallbackInfo ci) {
        if (textComponent.equals(Component.translatable("disconnect.timeout")))
            DiscordIntegrationMod.timeouts.add(this.player.getUUID());
    }
}
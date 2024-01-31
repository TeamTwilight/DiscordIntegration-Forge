package de.erdbeerbaerlp.dcintegration.neoforge.mixin;

import com.mojang.authlib.GameProfile;
import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.storage.Configuration;
import de.erdbeerbaerlp.dcintegration.common.storage.Localization;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.LinkManager;
import de.erdbeerbaerlp.dcintegration.common.util.MinecraftPermission;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class MixinPlayerLogin {
    @Inject(method = "canPlayerLogin", at = @At("HEAD"), cancellable = true)
    private void canLogin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Component> cir) {
        if (Configuration.instance().linking.whitelistMode && ServerLifecycleHooks.getCurrentServer().usesAuthentication()) {
            LinkManager.checkGlobalAPI(profile.getId());
            if(DiscordIntegration.INSTANCE.getServerInterface().playerHasPermissions(profile.getId(), MinecraftPermission.BYPASS_WHITELIST,MinecraftPermission.ADMIN)){
                return;
            }
            try {
                if (!LinkManager.isPlayerLinked(profile.getId())) {
                    cir.setReturnValue(Component.literal(Localization.instance().linking.notWhitelistedCode.replace("%code%",""+LinkManager.genLinkNumber(profile.getId()))));
                }else if(!DiscordIntegration.INSTANCE.canPlayerJoin(profile.getId())){
                    cir.setReturnValue(Component.literal(Localization.instance().linking.notWhitelistedRole));
                }
            } catch (IllegalStateException e) {
                cir.setReturnValue(Component.literal("An error occured\nPlease check Server Log for more information\n\n" + e));
            }
        }
    }
}

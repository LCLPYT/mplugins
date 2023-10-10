package work.lclpnet.mplugins.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mplugins.MPluginsAPI;
import work.lclpnet.mplugins.ext.MPluginLib;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract boolean isDedicated();

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getNetworkIo()Lnet/minecraft/server/ServerNetworkIo;",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            method = "shutdown"
    )
    private void mplugins$beforeGetNetworkIo(CallbackInfo ci) {
        // call world unready on all loaded plugins
        var api = MPluginsAPI.get();
        api.setWorldReady(false);

        var pluginManager = api.getPluginFrame().getPluginManager();
        pluginManager.getPlugins().forEach(MPluginLib::notifyWorldUnready);
    }

    @Inject(
            method = "shutdown",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer$ResourceManagerHolder;close()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void mplugins$beforeResourceClose(CallbackInfo ci) {
        // plugins should only be unloaded on dedicated servers here. on clients unload at client shutdown.
        if (!this.isDedicated()) return;

        var api = MPluginsAPI.get();
        var pluginManager = api.getPluginFrame().getPluginManager();

        pluginManager.shutdown();
    }
}

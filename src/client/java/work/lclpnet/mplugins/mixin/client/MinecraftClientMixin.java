package work.lclpnet.mplugins.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mplugins.MPlugins;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(
            method = "close",
            at = @At("HEAD")
    )
    public void mplugins$close(CallbackInfo ci) {
        // unload plugins when closing the MinecraftClient
        var api = MPlugins.getAPI();
        var pluginManager = api.getPluginFrame().getPluginManager();

        pluginManager.shutdown();
    }
}

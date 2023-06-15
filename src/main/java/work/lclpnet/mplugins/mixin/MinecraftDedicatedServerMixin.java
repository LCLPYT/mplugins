package work.lclpnet.mplugins.mixin;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mplugins.MPluginsAPI;
import work.lclpnet.mplugins.ext.MPluginLib;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin {

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V",
					shift = At.Shift.AFTER
			),
			method = "setupServer"
	)
	private void mplugins$afterWorldLoad(CallbackInfoReturnable<Boolean> cir) {
		// call world ready on all loaded plugins
		var api = MPluginsAPI.get();
		var pluginManager = api.getPluginFrame().getPluginManager();

		api.setWorldReady(true);

		pluginManager.getPlugins().forEach(MPluginLib::notifyWorldReady);
	}
}
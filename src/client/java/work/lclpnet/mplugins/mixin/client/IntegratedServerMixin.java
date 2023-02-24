package work.lclpnet.mplugins.mixin.client;

import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.ext.MPluginLib;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/integrated/IntegratedServer;loadWorld()V",
					shift = At.Shift.AFTER
			),
			method = "setupServer"
	)
	private void mplugins$afterWorldLoad(CallbackInfoReturnable<Boolean> cir) {
		// call world ready on all loaded plugins
		var api = MPlugins.getAPI();
		var pluginManager = api.getPluginFrame().getPluginManager();

		api.setWorldReady(true);

		pluginManager.getPlugins().forEach(MPluginLib::notifyWorldReady);
	}
}
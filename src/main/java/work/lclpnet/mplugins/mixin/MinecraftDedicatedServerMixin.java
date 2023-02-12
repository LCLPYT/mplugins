package work.lclpnet.mplugins.mixin;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.PluginFrame;

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
		var api = MPlugins.getAPI();
		var pluginManager = api.getPluginFrame().getPluginManager();

		api.setReady(true);

		pluginManager.getPlugins().forEach(PluginFrame::enablePlugin);
	}
}
package work.lclpnet.mplugins.mixin;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.ext.FabricPlugin;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftServerMixin {

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V",
					shift = At.Shift.AFTER
			),
			method = "setupServer"
	)
	private void mplugins$afterWorldLoad(CallbackInfoReturnable<Boolean> cir) {
		var pluginManager = MPlugins.getAPI().getPluginFrame().getPluginManager();

		pluginManager.getPlugins().forEach(loadedPlugin -> {
			if (loadedPlugin.getPlugin() instanceof FabricPlugin fabricPlugin) {
				fabricPlugin.onReady();
			}
		});
	}
}
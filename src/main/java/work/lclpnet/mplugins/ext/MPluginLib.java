package work.lclpnet.mplugins.ext;

import net.minecraft.server.MinecraftServer;
import work.lclpnet.mplugins.MPluginsAPI;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.util.HashSet;

public class MPluginLib {

    private static final HashSet<LoadedPlugin> worldReady = new HashSet<>();

    public static void notifyWorldReady(LoadedPlugin plugin) {
        if (!(plugin.getPlugin() instanceof WorldStateListener listener) || !MPluginsAPI.get().isWorldReady()) return;

        synchronized (worldReady) {
            if (worldReady.contains(plugin)) return;

            worldReady.add(plugin);
        }

        listener.onWorldReady();
    }

    public static void notifyWorldUnready(LoadedPlugin plugin) {
        if (!(plugin.getPlugin() instanceof WorldStateListener listener)) return;

        synchronized (worldReady) {
            if (!worldReady.contains(plugin)) return;

            worldReady.remove(plugin);
        }

        listener.onWorldUnready();
    }

    public static void updateServerReference(LoadedPlugin plugin) {
        if (!(plugin.getPlugin() instanceof FabricPlugin fabricPlugin)) return;

        PluginEnvironment environment = fabricPlugin.getEnvironment();
        if (!(environment instanceof MPluginsEnvironment env)) return;

        MinecraftServer server = MPluginsAPI.get().getServer();
        env.setServer(server);
    }
}

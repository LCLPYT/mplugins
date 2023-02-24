package work.lclpnet.mplugins.ext;

import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.util.HashSet;

public class MPluginLib {

    private static final HashSet<LoadedPlugin> worldReady = new HashSet<>();

    public static void notifyWorldReady(LoadedPlugin plugin) {
        if (!(plugin.getPlugin() instanceof WorldStateListener listener) || !MPlugins.getAPI().isWorldReady()) return;

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
}

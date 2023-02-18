package work.lclpnet.mplugins.ext;

import work.lclpnet.plugin.load.LoadedPlugin;

public class MPluginExt {

    public static void callWorldReady(LoadedPlugin loadedPlugin) {
        if (loadedPlugin.getPlugin() instanceof WorldStateListener worldStateListener) {
            worldStateListener.onWorldReady();
        }
    }

    public static void callWorldUnready(LoadedPlugin loadedPlugin) {
        if (loadedPlugin.getPlugin() instanceof WorldStateListener worldStateListener) {
            worldStateListener.onWorldUnready();
        }
    }
}

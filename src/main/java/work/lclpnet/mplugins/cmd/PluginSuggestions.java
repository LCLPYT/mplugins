package work.lclpnet.mplugins.cmd;

import work.lclpnet.plugin.PluginManager;

public class PluginSuggestions {

    private static LoadedPluginSuggestionProvider loadedPluginProvider = null;

    public static void init(final PluginManager pluginManager) {
        loadedPluginProvider = new LoadedPluginSuggestionProvider(pluginManager);
    }

    public static LoadedPluginSuggestionProvider getLoadedPluginProvider() {
        return loadedPluginProvider;
    }
}

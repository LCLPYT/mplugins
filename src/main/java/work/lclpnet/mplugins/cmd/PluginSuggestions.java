package work.lclpnet.mplugins.cmd;

import work.lclpnet.mplugins.PluginFrame;

public class PluginSuggestions {

    private static LoadedPluginSuggestionProvider loadedPluginProvider = null;
    private static LoadablePluginSuggestionProvider loadablePluginProvider = null;

    public static void init(final PluginFrame frame) {
        var pluginManager = frame.getPluginManager();

        loadedPluginProvider = new LoadedPluginSuggestionProvider(pluginManager);
        loadablePluginProvider = new LoadablePluginSuggestionProvider(frame);
    }

    public static LoadedPluginSuggestionProvider loadedPluginProvider() {
        return loadedPluginProvider;
    }

    public static LoadablePluginSuggestionProvider loadablePluginProvider() {
        return loadablePluginProvider;
    }
}

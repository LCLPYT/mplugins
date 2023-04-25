package work.lclpnet.mplugins.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPluginsAPI;
import work.lclpnet.plugin.Plugin;

public class MPluginsLoggerSupplier implements PluginLoggerSupplier {

    @Override
    public Logger getLogger(Plugin plugin) {
        var loadedPlugin = MPluginsAPI.get().getPluginFrame().getPluginManager().getPlugin(plugin);

        if (loadedPlugin.isPresent()) {
            return LoggerFactory.getLogger(loadedPlugin.get().getId());
        } else {
            return LoggerFactory.getLogger(this.getClass());
        }
    }
}

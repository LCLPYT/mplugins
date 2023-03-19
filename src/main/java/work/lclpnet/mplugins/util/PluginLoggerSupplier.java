package work.lclpnet.mplugins.util;

import org.slf4j.Logger;
import work.lclpnet.plugin.Plugin;

public interface PluginLoggerSupplier {

    Logger getLogger(Plugin plugin);
}

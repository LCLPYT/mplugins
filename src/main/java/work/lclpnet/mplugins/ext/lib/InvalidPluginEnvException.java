package work.lclpnet.mplugins.ext.lib;

import work.lclpnet.plugin.load.PluginLoadException;

import java.io.Serial;

public class InvalidPluginEnvException extends PluginLoadException {

    @Serial
    private static final long serialVersionUID = 7229295253634906568L;

    public InvalidPluginEnvException(String msg) {
        super(msg);
    }
}

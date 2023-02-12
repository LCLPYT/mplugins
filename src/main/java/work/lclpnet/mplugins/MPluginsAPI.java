package work.lclpnet.mplugins;

import work.lclpnet.mplugins.config.Config;

public interface MPluginsAPI {

    Config getConfig();

    PluginFrame getPluginFrame();

    boolean isReady();

    void setReady(boolean ready);
}

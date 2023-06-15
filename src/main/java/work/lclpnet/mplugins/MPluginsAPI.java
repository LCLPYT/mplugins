package work.lclpnet.mplugins;

import net.minecraft.server.MinecraftServer;
import work.lclpnet.mplugins.config.Config;

import javax.annotation.Nullable;

public interface MPluginsAPI {

    Config getConfig();

    PluginFrame getPluginFrame();

    boolean isWorldReady();

    void setWorldReady(boolean ready);

    @Nullable
    MinecraftServer getServer();

    static MPluginsAPI get() {
        return MPlugins.getAPI();
    }
}

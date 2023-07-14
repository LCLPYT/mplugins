package work.lclpnet.mplugins;

import net.minecraft.server.MinecraftServer;
import work.lclpnet.mplugins.config.ConfigAccess;

import javax.annotation.Nullable;

public interface MPluginsAPI extends ConfigAccess {

    PluginFrame getPluginFrame();

    boolean isWorldReady();

    void setWorldReady(boolean ready);

    @Nullable
    MinecraftServer getServer();

    static MPluginsAPI get() {
        return MPlugins.getAPI();
    }
}

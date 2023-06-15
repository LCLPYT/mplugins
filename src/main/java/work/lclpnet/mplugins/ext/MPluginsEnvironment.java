package work.lclpnet.mplugins.ext;

import net.minecraft.server.MinecraftServer;

public class MPluginsEnvironment implements PluginEnvironment {

    private final Object serverMutex = new Object();
    private MinecraftServer server = null;

    @Override
    public MinecraftServer getServer() {
        synchronized (serverMutex) {
            return server;
        }
    }

    public void setServer(MinecraftServer server) {
        synchronized (serverMutex) {
            this.server = server;
        }
    }
}

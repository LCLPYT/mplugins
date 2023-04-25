package work.lclpnet.test;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.MinecraftVersion;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.ext.FabricPlugin;
import work.lclpnet.mplugins.ext.WorldStateListener;

public class TestPlugin extends FabricPlugin implements WorldStateListener {

    public static final String ID = "testPlugin";
    private static final Logger logger = LoggerFactory.getLogger(ID);

    @Override
    public void loadFabricPlugin() {
        logger.info("Test plugin loaded.");

        // plugins have full access to Minecraft, Fabric and MPlugins
        var fabric = FabricLoader.getInstance();
        var mplugins = fabric.getModContainer(MPlugins.MOD_ID).orElseThrow();

        logger.info("Running Minecraft {} with Fabric {} and mplugins {}",
                MinecraftVersion.CURRENT.getName(), FabricLoaderImpl.VERSION, mplugins.getMetadata().getVersion());

        // for other classes implementing the Unloadable interface, the registerUnloadable() method can be used
        // in order to automatically unload them.
        // registerUnloadable(someUnloadableInstance);

        // if you need a reference to MinecraftServer instance, use this method
        MinecraftServer server = getEnvironment().getServer();
        if (server == null) {
            logger.info("The Minecraft server is not yet active");
        } else {
            logger.info("A '{}' Minecraft server is active.", server.getServerModName());
        }
    }

    @Override
    public void onWorldReady() {
        // world is loaded
        logger.info("Test plugin is ready.");
    }

    @Override
    public void onWorldUnready() {
        // world is going to unload
        logger.info("Test plugin is unready.");
    }

    @Override
    public void unloadFabricPlugin() {
        logger.info("Test plugin unloaded.");

        // registered Unloadables will be unloaded after this method automatically
    }
}

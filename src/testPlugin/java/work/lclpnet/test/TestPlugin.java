package work.lclpnet.test;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.MinecraftVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.ext.WorldStateListener;
import work.lclpnet.plugin.Plugin;

public class TestPlugin implements Plugin, WorldStateListener {

    public static final String ID = "testPlugin";
    private static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
    public void load() {
        LOGGER.info("Test plugin loaded.");
    }

    @Override
    public void onWorldReady() {
        // world is loaded
        LOGGER.info("Test plugin is ready.");

        // full access to Minecraft, Fabric and MPlugins
        var fabric = FabricLoader.getInstance();
        var mplugins = fabric.getModContainer(MPlugins.MOD_ID).orElseThrow();

        LOGGER.info("Running Minecraft {} with Fabric {} and mplugins {}",
                MinecraftVersion.CURRENT.getName(), FabricLoaderImpl.VERSION, mplugins.getMetadata().getVersion());
    }

    @Override
    public void onWorldUnready() {
        // world is going to unload
        LOGGER.info("Test plugin is unready.");
    }

    @Override
    public void unload() {
        LOGGER.info("Test plugin unloaded.");
    }
}

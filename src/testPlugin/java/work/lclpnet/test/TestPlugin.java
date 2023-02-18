package work.lclpnet.test;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.MinecraftVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.ext.FabricPlugin;
import work.lclpnet.plugin.Plugin;

public class TestPlugin implements Plugin, FabricPlugin {

    public static final String ID = "testPlugin";
    private static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
    public void load() {
        LOGGER.info("Test plugin loaded.");
    }

    @Override
    public void onReady() {
        LOGGER.info("Test plugin is ready.");

        // full access to Minecraft, Fabric and MPlugins
        var fabric = FabricLoader.getInstance();
        var mplugins = fabric.getModContainer(MPlugins.MOD_ID).orElseThrow();

        LOGGER.info("Running Minecraft {} with Fabric {} and mplugins {}",
                MinecraftVersion.CURRENT.getName(), FabricLoaderImpl.VERSION, mplugins.getMetadata().getVersion());
    }

    @Override
    public void unload() {
        LOGGER.info("Test plugin unloaded.");
    }
}

package work.lclpnet.test;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.MinecraftVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.ext.FabricPlugin;
import work.lclpnet.mplugins.ext.WorldStateListener;
import work.lclpnet.test.hook.TestHookListener;

public class TestPlugin extends FabricPlugin implements WorldStateListener {

    public static final String ID = "testPlugin";
    private static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
    public void loadFabricPlugin() {
        LOGGER.info("Test plugin loaded.");

        // plugins have full access to Minecraft, Fabric and MPlugins
        var fabric = FabricLoader.getInstance();
        var mplugins = fabric.getModContainer(MPlugins.MOD_ID).orElseThrow();

        LOGGER.info("Running Minecraft {} with Fabric {} and mplugins {}",
                MinecraftVersion.CURRENT.getName(), FabricLoaderImpl.VERSION, mplugins.getMetadata().getVersion());

        // when using hooks, they must be unregistered when the plugin unloads.
        // plugins can use the registerHook and registerHooks methods for comfort.
        // these methods take care of unregistering the hooks at plugin unload
        registerHooks(new TestHookListener(getLogger()));

        // for other classes implementing the Unloadable interface, the registerUnloadable() method can be used
        // in order to automatically unload them.
        // registerUnloadable(someUnloadableInstance);
    }

    @Override
    public void onWorldReady() {
        // world is loaded
        LOGGER.info("Test plugin is ready.");
    }

    @Override
    public void onWorldUnready() {
        // world is going to unload
        LOGGER.info("Test plugin is unready.");

        // unregister events here, if you registered them in onWorldReady()
        // otherwise, all events are unregistered at plugin unload automatically
        // unregisterAllHooks();
    }

    @Override
    public void unloadFabricPlugin() {
        LOGGER.info("Test plugin unloaded.");

        // registered Unloadables (such as hooks) will be unloaded after this method automatically
    }
}

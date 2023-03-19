package work.lclpnet.mplugins;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.ext.FabricPlugin;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FabricPluginTest {

    private static final Logger logger = LoggerFactory.getLogger(FabricPluginTest.class);

    @Test
    public void testLoadableWithoutBootstrap() {
        // make sure FabricPlugin is loadable without a Minecraft instance
        var plugin = new FabricPlugin(x -> logger);
        assertThrows(IllegalStateException.class, plugin::getLogger);

        plugin.load();

        plugin.getLogger();
    }
}

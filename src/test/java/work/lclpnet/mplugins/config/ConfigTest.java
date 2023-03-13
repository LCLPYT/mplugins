package work.lclpnet.mplugins.config;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

    private static final String json = """
            {
              "pluginDirectory": "plugins",
              "commands": {
                "enableLoadCommand": true,
                "enableUnloadCommand": true
              },
              "bootstrap": {
                "autoLoadPlugins": true
              }
            }
            """.trim();

    @Test
    void deserialize() {
        var config = new Config(new JSONObject(json));

        assertEquals(Path.of("plugins"), config.pluginDirectory);
        assertTrue(config.enableLoadCommand);
        assertTrue(config.enableUnloadCommand);
    }

    @Test
    void serialize() {
        var config = new Config();

        var serialized = config.serialize();
        var expected = new JSONObject(json);

        assertBoth(expected, serialized, x -> x.getString("pluginDirectory"));
        assertBoth(expected, serialized, x -> x.getJSONObject("commands").getBoolean("enableLoadCommand"));
        assertBoth(expected, serialized, x -> x.getJSONObject("commands").getBoolean("enableUnloadCommand"));
        assertBoth(expected, serialized, x -> x.getJSONObject("bootstrap").getBoolean("autoLoadPlugins"));
    }

    private static <T> void assertBoth(T expected, T actual, Function<T, Object> getter) {
        var expectedVal = getter.apply(expected);
        var actualVal = getter.apply(actual);

        assertEquals(expectedVal, actualVal);
    }
}
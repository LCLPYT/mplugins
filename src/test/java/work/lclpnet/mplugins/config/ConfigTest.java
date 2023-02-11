package work.lclpnet.mplugins.config;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    private static final String json = """
            {
              "pluginDirectory": "plugins"
            }
            """.trim();

    @Test
    void deserialize() {
        var config = new Config(new JSONObject(json));

        assertEquals(Path.of("plugins"), config.pluginDirectory);
    }

    @Test
    void serialize() {
        var config = new Config();

        var serialized = config.serialize();
        var expected = new JSONObject(json);

        assertBoth(expected, serialized, x -> x.getString("pluginDirectory"));
    }

    private static <T> void assertBoth(T expected, T actual, Function<T, Object> getter) {
        var expectedVal = getter.apply(expected);
        var actualVal = getter.apply(actual);

        assertEquals(expectedVal, actualVal);
    }
}
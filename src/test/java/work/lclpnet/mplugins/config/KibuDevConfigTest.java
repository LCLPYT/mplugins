package work.lclpnet.mplugins.config;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KibuDevConfigTest {

    private static final Logger LOGGER = LoggerFactory.getLogger("test");

    @Test
    void testReadClasspath() {
        KibuDevConfig config = new KibuDevConfig(LOGGER);
        config.load(testConfig());

        final List<URL[]> expected = getClassPaths().stream()
                .map(paths -> Arrays.stream(paths).map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(URL[]::new))
                .toList();

        final List<URL[]> actual = config.getPluginPaths();

        assertNotNull(actual);

        assertEquals(expected.stream().map(Arrays::asList).collect(Collectors.toSet()),
                actual.stream().map(Arrays::asList).collect(Collectors.toSet()));
    }

    @NotNull
    private static JSONObject testConfig() {
        JSONObject config = new JSONObject();

        config.put("plugin_paths", getClassPaths());

        return config;
    }

    @NotNull
    private static List<Path[]> getClassPaths() {
        return Arrays.asList(
                new Path[]{Path.of("foo"), Path.of("bar")},
                new Path[]{Path.of("abc")}
        );
    }
}
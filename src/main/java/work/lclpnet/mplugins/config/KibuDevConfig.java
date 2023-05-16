package work.lclpnet.mplugins.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class KibuDevConfig {

    private final Logger logger;
    private JSONObject config = null;

    public KibuDevConfig(Logger logger) {
        this.logger = logger;
    }

    public boolean load() {
        String kdcProperty = System.getProperty("kibu-dev.config");
        if (kdcProperty == null) return false;

        Path kdcPath = Path.of(kdcProperty);

        return load(kdcPath);
    }

    public boolean load(Path kdcPath) {
        String json;

        try {
            json = Files.readString(kdcPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read kibu-dev config", e);
            return false;
        }

        load(new JSONObject(json));

        return true;
    }

    public void load(@Nullable JSONObject config) {
        this.config = config;
    }

    @Nullable
    public List<URL[]> getPluginPaths() {
        if (config == null || !config.has("plugin_paths")) return null;

        JSONArray pluginPaths = config.getJSONArray("plugin_paths");
        List<URL[]> classpath = new ArrayList<>();

        for (int i = 0; i < pluginPaths.length(); i++) {
            Object maybeTuple = pluginPaths.get(i);

            if (!(maybeTuple instanceof JSONArray tuple)) {
                logger.warn("Invalid plugin_paths entry in kibu-dev config at index {}; expected array", i);
                continue;
            }

            List<URL> urls = new ArrayList<>();

            for (int j = 0; j < tuple.length(); j++) {
                Object maybeString = tuple.get(j);

                if (!(maybeString instanceof String string)) {
                    logger.warn("Invalid plugin_paths entry in kibu-dev config at index {}: {}; expected string", j, maybeString);
                    continue;
                }

                try {
                    urls.add(Path.of(string).toUri().toURL());
                } catch (MalformedURLException e) {
                    logger.error("Failed to parse os path {} in kibu-dev config", string, e);
                }
            }

            classpath.add(urls.toArray(URL[]::new));
        }

        return classpath;
    }
}

package work.lclpnet.mplugins.config;

import net.fabricmc.loader.api.FabricLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import work.lclpnet.mplugins.MPlugins;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigService {

    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve(MPlugins.MOD_ID).resolve("config.json");

    public static Config loadConfig(Logger logger) throws IOException {
        if (!Files.exists(FILE)) {
            var conf = new Config();

            try {
                saveConfig(conf);
            } catch (IOException e) {
                logger.error("Failed to save config", e);
            }

            return conf;
        }

        var content = Files.readString(FILE, StandardCharsets.UTF_8);
        var json = new JSONObject(content.trim());

        return new Config(json);
    }

    public static void saveConfig(Config config) throws IOException {
        var json = config.serialize();
        var content = json.toString(2);

        var dir = FILE.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Files.writeString(FILE, content, StandardCharsets.UTF_8);
    }
}

package work.lclpnet.mplugins.config;

import org.json.JSONObject;
import work.lclpnet.config.json.JsonConfig;
import work.lclpnet.config.json.JsonConfigFactory;

import java.nio.file.Path;

public class Config implements JsonConfig {

    public Path pluginDirectory = Path.of("plugins");
    public boolean enableLoadCommand = true;
    public boolean enableUnloadCommand = true;
    public boolean loadPluginsOnStartup = true;
    public boolean autoReloadPlugins = false;

    public Config() {}

    public Config(JSONObject obj) {
        if (obj.has("pluginDirectory")) {
            var pluginDirectory = obj.getString("pluginDirectory");
            this.pluginDirectory = Path.of(pluginDirectory);
        }

        if (obj.has("loader")) {
            var loader = obj.getJSONObject("loader");

            if (loader.has("loadPluginsOnStartup")) {
                this.loadPluginsOnStartup = loader.getBoolean("loadPluginsOnStartup");
            }

            if (loader.has("autoReloadPlugins")) {
                this.autoReloadPlugins = loader.getBoolean("autoReloadPlugins");
            }
        }

        if (obj.has("commands")) {
            var commands = obj.getJSONObject("commands");

            if (commands.has("enableLoadCommand")) {
                this.enableLoadCommand = commands.getBoolean("enableLoadCommand");
            }

            if (commands.has("enableUnloadCommand")) {
                this.enableUnloadCommand = commands.getBoolean("enableUnloadCommand");
            }
        }
    }

    @Override
    public JSONObject toJson() {
        var json = new JSONObject();

        json.put("pluginDirectory", pluginDirectory.toString());

        var commands = new JSONObject();
        commands.put("enableLoadCommand", enableLoadCommand);
        commands.put("enableUnloadCommand", enableUnloadCommand);

        json.put("commands", commands);

        var loader = new JSONObject();
        loader.put("loadPluginsOnStartup", loadPluginsOnStartup);
        loader.put("autoReloadPlugins", autoReloadPlugins);

        json.put("loader", loader);

        return json;
    }

    static final JsonConfigFactory<Config> FACTORY = new JsonConfigFactory<>() {
        @Override
        public Config createDefaultConfig() {
            return new Config();
        }

        @Override
        public Config createConfig(JSONObject json) {
            return new Config(json);
        }
    };
}

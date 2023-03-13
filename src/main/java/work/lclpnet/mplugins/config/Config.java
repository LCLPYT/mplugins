package work.lclpnet.mplugins.config;

import org.json.JSONObject;

import java.nio.file.Path;

public class Config {

    public Path pluginDirectory = Path.of("plugins");
    public boolean enableLoadCommand = true;
    public boolean enableUnloadCommand = true;
    public boolean autoLoadPlugins = true;

    public Config() {}

    public Config(JSONObject obj) {
        if (obj.has("pluginDirectory")) {
            var pluginDirectory = obj.getString("pluginDirectory");
            this.pluginDirectory = Path.of(pluginDirectory);
        }

        if (obj.has("bootstrap")) {
            var bootstrap = obj.getJSONObject("bootstrap");

            if (bootstrap.has("autoLoadPlugins")) {
                this.autoLoadPlugins = bootstrap.getBoolean("autoLoadPlugins");
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

    public JSONObject serialize() {
        var json = new JSONObject();

        json.put("pluginDirectory", pluginDirectory.toString());

        var commands = new JSONObject();
        commands.put("enableLoadCommand", enableLoadCommand);
        commands.put("enableUnloadCommand", enableUnloadCommand);

        json.put("commands", commands);

        var bootstrap = new JSONObject();
        bootstrap.put("autoLoadPlugins", autoLoadPlugins);

        json.put("bootstrap", bootstrap);

        return json;
    }
}

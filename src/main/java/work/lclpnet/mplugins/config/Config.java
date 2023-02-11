package work.lclpnet.mplugins.config;

import org.json.JSONObject;

import java.nio.file.Path;

public class Config {

    public Path pluginDirectory = Path.of("plugins");

    public Config() {}

    public Config(JSONObject object) {
        if (object.has("pluginDirectory")) {
            var pluginDirectory = object.getString("pluginDirectory");
            this.pluginDirectory = Path.of(pluginDirectory);
        }
    }

    public JSONObject serialize() {
        var json = new JSONObject();

        json.put("pluginDirectory", pluginDirectory.toString());

        return json;
    }
}

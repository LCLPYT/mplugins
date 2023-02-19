package work.lclpnet.mplugins.ext.fabric;

import net.fabricmc.api.EnvType;
import org.json.JSONObject;
import work.lclpnet.plugin.manifest.BasePluginManifest;
import work.lclpnet.plugin.manifest.JsonManifestLoader;
import work.lclpnet.plugin.manifest.PluginManifest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class FabricJsonManifestLoader extends JsonManifestLoader {

    @Override
    public PluginManifest loadFromJson(JSONObject obj) {
        var base = (BasePluginManifest) super.loadFromJson(obj);

        var validEnvTypes = Arrays.stream(EnvType.values())
                .map(EnvType::name)
                .map(String::toLowerCase)
                .toList();

        optional(obj, "env", array(x -> x instanceof String && validEnvTypes.contains(x)));
        var env = obj.has("env") ? stream(obj.getJSONArray("env"))
                .map(String::valueOf)
                .collect(Collectors.toUnmodifiableSet()) : Set.copyOf(validEnvTypes);

        optional(obj, "requires", array(x -> x instanceof String));
        var requires = obj.has("requires") ? stream(obj.getJSONArray("requires"))
                .map(String::valueOf)
                .collect(Collectors.toUnmodifiableSet()) : Collections.<String>emptySet();

        return new FabricPluginManifest(base, env, requires);
    }
}

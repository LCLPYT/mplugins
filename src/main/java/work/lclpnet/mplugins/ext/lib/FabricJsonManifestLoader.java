package work.lclpnet.mplugins.ext.lib;

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
        final var base = (BasePluginManifest) super.loadFromJson(obj);

        final var validEnvTypes = Arrays.stream(EnvType.values())
                .map(EnvType::name)
                .map(String::toLowerCase)
                .toList();

        Set<String> env = Set.copyOf(validEnvTypes);
        Set<String> requires = Collections.emptySet();

        if (obj.has("fabric")) {
            var fabric = obj.getJSONObject("fabric");

            optional(fabric, "env", array(x -> x instanceof String && validEnvTypes.contains(x)));
            env = fabric.has("env") ? stream(fabric.getJSONArray("env"))
                    .map(String::valueOf)
                    .collect(Collectors.toUnmodifiableSet()) : Set.copyOf(validEnvTypes);

            optional(fabric, "requires", array(x -> x instanceof String));
            requires = fabric.has("requires") ? stream(fabric.getJSONArray("requires"))
                    .map(String::valueOf)
                    .collect(Collectors.toUnmodifiableSet()) : Collections.emptySet();
        }

        return new FabricPluginManifest(base, env, requires);
    }
}

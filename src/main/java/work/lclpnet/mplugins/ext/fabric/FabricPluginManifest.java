package work.lclpnet.mplugins.ext.fabric;

import work.lclpnet.plugin.manifest.BasePluginManifest;
import work.lclpnet.plugin.manifest.PluginManifest;

import java.util.Set;

public class FabricPluginManifest implements PluginManifest {

    private final BasePluginManifest base;
    private final Set<String> env;
    private final Set<String> requires;

    public FabricPluginManifest(BasePluginManifest base, Set<String> env, Set<String> requires) {
        this.base = base;
        this.env = env;
        this.requires = requires;
    }

    @Override
    public String version() {
        return base.version();
    }

    @Override
    public String id() {
        return base.id();
    }

    @Override
    public String entryPoint() {
        return base.entryPoint();
    }

    @Override
    public Set<String> dependsOn() {
        return base.dependsOn();
    }

    public Set<String> env() {
        return env;
    }

    public Set<String> requires() {
        return requires;
    }
}

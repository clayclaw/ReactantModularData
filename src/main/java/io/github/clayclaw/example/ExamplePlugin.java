package io.github.clayclaw.example;

import dev.reactant.reactant.core.ReactantPlugin;
import dev.reactant.xaku.manager.dependency.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;

@ReactantPlugin(servicePackages = { "io.github.clayclaw.example" })
public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginHook.getInstance(this);
    }

}

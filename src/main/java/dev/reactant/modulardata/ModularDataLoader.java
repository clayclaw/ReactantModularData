package dev.reactant.modulardata;

import dev.reactant.reactant.core.ReactantPlugin;
import dev.reactant.xaku.manager.dependency.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;

@ReactantPlugin(servicePackages = { "dev.reactant.modulardata" })
public class ModularDataLoader extends JavaPlugin {

    private static ModularDataLoader INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        PluginHook.getInstance(this);
    }

    public static ModularDataLoader getINSTANCE() {
        return INSTANCE;
    }
}

package net.arcev.test;

import lombok.Getter;
import lombok.Setter;
import net.arcev.test.commands.CommandManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Setter
    @Getter
    private CommandManager cmdmanager;

    @Getter
    @Setter
    private static Main plugin;

    @Override
    public void onEnable() {

        Main.plugin = this;

        try {
            //Initialize CommandManager
            cmdmanager = new CommandManager(this);
            cmdmanager.init();
        } catch (final Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to initialize the command manager.");
            /* Disable the plugin */
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }


    @Override
    public void onDisable() {}

}

package me.stivendarsi.orbit;

import me.stivendarsi.orbit.command.CommandHandler;
import me.stivendarsi.orbit.events.DialogSoundHandler;
import me.stivendarsi.orbit.experience.LoadUserExperienceHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Orbit extends JavaPlugin {

    private static Orbit orbit;
    public static Orbit plugin(){
        return orbit;
    }

    private static MainHandler mainHandler;
    public static MainHandler mainHandler(){
        return mainHandler;
    }

    @Override
    public void onEnable() {
        orbit = this;
        mainHandler = new MainHandler();

        saveDefaultConfig();
        reloadConfig();

        mainHandler.load();

        CommandHandler.register(this.getLifecycleManager());
        getServer().getPluginManager().registerEvents(new LoadUserExperienceHandler(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

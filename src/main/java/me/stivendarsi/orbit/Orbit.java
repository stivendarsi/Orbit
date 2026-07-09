package me.stivendarsi.orbit;

import me.stivendarsi.orbit.command.CommandHandler;
import me.stivendarsi.orbit.events.EntityKillQuestHandler;
import me.stivendarsi.orbit.events.LoadUserExperienceHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Orbit extends JavaPlugin {

    private static Orbit orbit;
    public static Orbit orbitInstance(){
        return orbit;
    }

    private static MainHandler mainHandler;
    public static MainHandler mainHandler() {
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
        getServer().getPluginManager().registerEvents(new EntityKillQuestHandler(), this);

        mainHandler().questHandler().startDailyQuestChanging(); // start the task for changing quests.
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

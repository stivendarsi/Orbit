package me.stivendarsi.orbit;

import io.github.miniplaceholders.api.Expansion;
import me.stivendarsi.orbit.command.CommandHandler;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.placeholders.OrbitPlaceholders;
import me.stivendarsi.orbit.quest.events.*;
import me.stivendarsi.orbit.events.LoadUserExperienceHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.UUID;

public final class Orbit extends JavaPlugin {

    private static Orbit orbit;

    public static Orbit orbitInstance() {
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
        getServer().getPluginManager().registerEvents(new FishQuestHandler(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakQuestHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathQuestHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayTimeQuestHandler(), this);

        mainHandler().questHandler().startDailyQuestChanging(); // start the task for changing quests.
        mainHandler().questHandler().startSeasonQuestChanging();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new OrbitPlaceholders().register();
        }
        Expansion expansion = Expansion.builder("orbit").audiencePlaceholder("stars", (audience, queue, ctx) -> {
            if (!(audience instanceof Player player)) return Tag.inserting(Component.empty());
            LocalUserData localUserData = mainHandler.userHandler().getUser(player.getUniqueId());
            OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
            if (currentOrbit == null || localUserData == null) return Tag.inserting(Component.empty());
            int stars = localUserData.getUserExperience(currentOrbit.identifier());
            return Tag.preProcessParsed(NumberFormat.getNumberInstance().format(stars));
        }).build();

        expansion.register();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mainHandler().unLoad();
    }
}

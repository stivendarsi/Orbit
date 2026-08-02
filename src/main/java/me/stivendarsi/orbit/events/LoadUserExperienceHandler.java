package me.stivendarsi.orbit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class LoadUserExperienceHandler implements Listener {
    @EventHandler
    public void loadUserExperience(PlayerJoinEvent event) {
        UUID userUUID = event.getPlayer().getUniqueId();

        mainHandler().userHandler().loadUser(userUUID);
        mainHandler().questHandler().loadUserQuestData(userUUID);

     //   LocalDateTime localDateTime = LocalDateTime.now();
    //    orbitInstance().getLogger().warning(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " Loaded user: " + event.getPlayer().getName());
    }

    @EventHandler
    public void unloadUserExperience(PlayerQuitEvent event) {
        UUID userUUID = event.getPlayer().getUniqueId();

        mainHandler().questHandler().saveUserQuestData(userUUID);
        mainHandler().userHandler().unloadUser(userUUID);

     //   LocalDateTime localDateTime = LocalDateTime.now();
    //    orbitInstance().getLogger().warning(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +" Saved user: " + event.getPlayer().getName());
    }
}

package me.stivendarsi.orbit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class LoadUserExperienceHandler implements Listener {
    @EventHandler
    public void loadUserExperience(PlayerJoinEvent event){
        UUID userUUID = event.getPlayer().getUniqueId();
        mainHandler().experienceHandler().loadUserFromRedis(userUUID);
    }

    @EventHandler
    public void unloadUserExperience(PlayerQuitEvent event){
        UUID userUUID = event.getPlayer().getUniqueId();
        mainHandler().experienceHandler().saveUserInRedis(userUUID);
    }
}

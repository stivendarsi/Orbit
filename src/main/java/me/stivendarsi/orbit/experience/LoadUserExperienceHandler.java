package me.stivendarsi.orbit.experience;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class LoadUserExperienceHandler implements Listener {
    @EventHandler
    public void loadUserExperience(PlayerJoinEvent event){
        UUID userUUID = event.getPlayer().getUniqueId();
        mainHandler().experienceHandler().loadUserFromRedis(userUUID);
    }
}

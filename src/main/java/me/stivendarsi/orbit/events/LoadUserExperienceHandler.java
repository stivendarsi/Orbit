package me.stivendarsi.orbit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class LoadUserExperienceHandler implements Listener {
    @EventHandler
    public void loadUserExperience(AsyncPlayerPreLoginEvent event) {
        UUID userUUID = event.getUniqueId();

        mainHandler().userHandler().loadUser(userUUID);
        mainHandler().questHandler().loadUserQuestData(userUUID);
    }

    @EventHandler
    public void unloadUserExperience(PlayerQuitEvent event) {
        UUID userUUID = event.getPlayer().getUniqueId();

        mainHandler().questHandler().saveUserQuestData(userUUID);
        mainHandler().userHandler().unloadUser(userUUID);
    }
}

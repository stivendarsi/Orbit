package me.stivendarsi.orbit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class UserDataHandler implements Listener {
    @EventHandler
    public void loadUserExperience(AsyncPlayerPreLoginEvent event) {
        UUID userUUID = event.getUniqueId();
        mainHandler().userHandler().registerUser(userUUID);

    }

    @EventHandler
    public void unloadUserExperience(PlayerQuitEvent event) {
        UUID userUUID = event.getPlayer().getUniqueId();
        mainHandler().userHandler().unregisterUser(userUUID);
    }
}

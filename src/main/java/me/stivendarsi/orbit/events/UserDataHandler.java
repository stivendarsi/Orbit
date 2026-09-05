package me.stivendarsi.orbit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class UserDataHandler implements Listener {
    @EventHandler
    public void loadUserData(PlayerJoinEvent event) {
        UUID userUUID = event.getPlayer().getUniqueId();
        mainHandler().userHandler().registerUser(userUUID);
    }

    @EventHandler
    public void unloadUserData(PlayerQuitEvent event) {
        UUID userUUID = event.getPlayer().getUniqueId();
        mainHandler().userHandler().unregisterUser(userUUID);
    }
}

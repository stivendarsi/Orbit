package me.stivendarsi.orbit.events;

import io.papermc.paper.event.player.PlayerCustomClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DialogSoundHandler implements Listener {
    @EventHandler
    public void onClick(PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof Player player)) return;
        player.sendRichMessage("hey");
    }
}

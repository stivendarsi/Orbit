package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class PlayTimeQuestHandler implements Listener {

    private static final Map<UUID, Long> playTime = new HashMap<>();

    public static Map<UUID, Long> playTime() {
        return playTime;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void track(PlayerJoinEvent event) {
        playTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        Player player = event.getPlayer();

        player.getScheduler().runAtFixedRate(orbitInstance(), scheduledTask -> {
            if (player.isOnline() || playTime.containsKey(player.getUniqueId())) updatePlayTime(player);
            else scheduledTask.cancel();


        }, null, 1, 20 * 60);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void untrack(PlayerQuitEvent event){
        updatePlayTime(event.getPlayer());
        playTime.remove(event.getPlayer().getUniqueId());
    }

    public void updatePlayTime(Player player) {
        UUID uuid = player.getUniqueId();

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            update(questData, uuid);
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        for (QuestData questData : orbitData.seasonQuests()) {
            update(questData, uuid);
        }
    }

    private void update(QuestData questData, UUID uuid) {
        if (questData == null || questData.questType() != QuestType.PLAY_TIME) return;
        //  questData.updateAndCheck(uuid, (int) questData.currentSessionTimePlayed(uuid).toMinutes());
    }
}

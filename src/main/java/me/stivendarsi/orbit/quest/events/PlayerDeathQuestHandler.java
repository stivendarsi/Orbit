package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class PlayerDeathQuestHandler implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        Player died = event.getPlayer();

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            update(questData, died.getUniqueId());
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        for (QuestData questData : orbitData.seasonQuests()) {
            update(questData, died.getUniqueId());
        }

    }

    private void update(QuestData questData, UUID uuid) {
        if (questData == null || questData.questType() != QuestType.PLAYER_DEATH) return;
        questData.updateAndCheck(uuid, 1);
    }
}

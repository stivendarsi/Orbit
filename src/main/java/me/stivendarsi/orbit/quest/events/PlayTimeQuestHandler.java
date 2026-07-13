package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class PlayTimeQuestHandler implements Listener {
    @EventHandler
    public void savePlayTime(PlayerStatisticIncrementEvent event) {
        if (event.isCancelled()) return;
        if (event.getStatistic() != Statistic.PLAY_ONE_MINUTE) return;
        Player player = event.getPlayer();


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

    private void update(QuestData questData, UUID uuid){
        if (questData == null || questData.questType() != QuestType.PLAY_TIME) return;
        questData.updateAndCheck(uuid, 1);
    }
}

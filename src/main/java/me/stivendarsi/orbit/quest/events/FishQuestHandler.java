package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class FishQuestHandler implements Listener {
    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.isCancelled()) return;
        Player cause = event.getPlayer();

        Entity fish = event.getCaught();
        if (fish == null) return;
        EntityType entityType = fish.getType();

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            update(questData, cause.getUniqueId(), entityType);
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        for (QuestData questData : orbitData.seasonQuests()) {
            update(questData, cause.getUniqueId(), entityType);
        }
    }

    private void update(QuestData questData, UUID uuid, EntityType entityType) {
        if (questData == null || questData.questType() != QuestType.FISHING) return;

        boolean entityTypeIsAllowedToFish = questData.allowedEntities().contains(entityType);

        if (!entityTypeIsAllowedToFish) {
            System.out.println("EntityType: " + entityType);
            return;
        }
        questData.updateAndCheck(uuid, 1);

    }
}

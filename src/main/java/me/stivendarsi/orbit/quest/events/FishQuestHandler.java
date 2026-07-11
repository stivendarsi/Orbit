package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

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
            if (questData == null || questData.questType() != QuestType.FISHING) continue;

            boolean entityTypeIsAllowedToFish = questData.allowedEntities().contains(entityType);

            if (!entityTypeIsAllowedToFish) {
                System.out.println("EntityType: " + entityType);
                return;
            }

            questData.countUser(cause.getUniqueId(), 1);

            boolean rewardPlayer = questData.getUserCount(cause.getUniqueId()) == questData.requiredAmount();

            if (rewardPlayer) {
                Constants.runCommandInConsole(cause, questData.rewardCommand()); // Reward the user if he is currently at the reached amount
                cause.sendRichMessage("<green>קיבלת כוכבים");
            }
        }
    }
}

package me.stivendarsi.orbit.quest.events;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.quest.Quest;
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

        LocalUserData userData = mainHandler().userHandler().getUser(cause.getUniqueId());
        Preconditions.checkNotNull(userData);

        Entity fish = event.getCaught();
        if (fish == null) return;
        EntityType entityType = fish.getType();

        for (Quest quest : mainHandler().questHandler().dailyQuests()) {
            if (quest == null || quest.questType() != QuestType.FISHING) continue;

            boolean entityTypeIsAllowedToFish = quest.allowedEntities().contains(entityType);

            if (!entityTypeIsAllowedToFish) {
                System.out.println("EntityType: " + entityType);
                return;
            }

            quest.countUser(cause.getUniqueId(), 1);

            boolean rewardPlayer = quest.getUserCount(cause.getUniqueId()) == quest.requiredAmount();

            if (rewardPlayer) {
                Constants.runCommandInConsole(cause, quest.rewardCommand()); // Reward the user if he is currently at the reached amount
                cause.sendRichMessage("<green>קיבלת כוכבים");
            }
        }
    }
}

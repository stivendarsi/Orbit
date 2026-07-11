package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class PlayerDeathQuestHandler implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        Player died = event.getPlayer();

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            if (questData == null || questData.questType() != QuestType.PLAYER_DEATH) continue;

            questData.countUser(died.getUniqueId(), 1);

            boolean rewardPlayer = questData.getUserCount(died.getUniqueId()) == questData.requiredAmount();

            if (rewardPlayer) {
                Constants.runCommandInConsole(died, questData.rewardCommand()); // Reward the user if he is currently at the reached amount
                died.sendRichMessage("<green>קיבלת כוכבים");
            }
        }
    }
}

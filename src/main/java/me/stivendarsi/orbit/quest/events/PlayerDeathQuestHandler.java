package me.stivendarsi.orbit.quest.events;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.quest.Quest;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class PlayerDeathQuestHandler implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        Player died = event.getPlayer();

        for (Quest quest : mainHandler().questHandler().dailyQuests()) {
            if (quest == null || quest.questType() != QuestType.PLAYER_DEATH) continue;

            quest.countUser(died.getUniqueId(), 1);

            boolean rewardPlayer = quest.getUserCount(died.getUniqueId()) == quest.requiredAmount();

            if (rewardPlayer) {
                Constants.runCommandInConsole(died, quest.rewardCommand()); // Reward the user if he is currently at the reached amount
                died.sendRichMessage("<green>קיבלת כוכבים");
            }
        }
    }
}

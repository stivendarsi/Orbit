package me.stivendarsi.orbit.quest.events;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.quest.enums.QuestType;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class EntityKillQuestHandler implements Listener {
    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof Player killer)) return;

        LocalUserData userData = mainHandler().userHandler().getUser(killer.getUniqueId());
        Preconditions.checkNotNull(userData);


        UUID killed = event.getEntity().getUniqueId();
        EntityType entityType = event.getEntity().getType();
        System.out.println(entityType);

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            if (questData == null || questData.questType() != QuestType.KILL_ENTITY) continue;

            boolean entityTypeIsAllowedToKill = questData.allowedEntities().contains(entityType);
            boolean entityKilledAlready = userData.getKilledEntities(questData.questIdentifier()).contains(killed);

            if (!entityTypeIsAllowedToKill || entityKilledAlready) {
                System.out.println("Entity allowed: " + entityType);
                System.out.println("Entity killed already: " + entityKilledAlready);
                return;
            }

            questData.countUser(killer.getUniqueId(), 1);

            boolean rewardPlayer = questData.getUserCount(killer.getUniqueId()) == questData.requiredAmount();

            if (rewardPlayer) {
                Constants.runCommandInConsole(killer, questData.rewardCommand()); // Reward the user if he is currently at the reached amount
                killer.sendRichMessage("<green>קיבלת כוכבים");
            }
            if (questData.getUserCount(killer.getUniqueId()) <= questData.requiredAmount()) {
                userData.countKill(questData.questIdentifier(), killed);
                killer.sendRichMessage("<green>נחשב הריגה");
            }
        }
    }
}

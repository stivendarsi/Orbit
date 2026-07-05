package me.stivendarsi.orbit.events;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.experience.enums.QuestType;
import me.stivendarsi.orbit.experience.Quest;
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

        for (Quest quest : mainHandler().questHandler().dailyQuests()) {
            if (quest == null || quest.questType() != QuestType.KILL_ENTITY) continue;

            boolean entityTypeIsAllowedToKill = quest.allowedEntities().contains(entityType);
            boolean entityKilledAlready = userData.getKilledEntities(quest.questIdentifier()).contains(killed);

            if (!entityTypeIsAllowedToKill || entityKilledAlready) {
                System.out.println("Entity allowed: " + entityType);
                System.out.println("Entity killed already: " + entityKilledAlready);
                return;
            }

            quest.countUser(killer.getUniqueId(), 1);

            boolean rewardPlayer = quest.getUserCount(killer.getUniqueId()) == quest.requiredAmount();

            if (rewardPlayer) Constants.runCommandInConsole(killer, quest.rewardCommand()); // Reward the user if he is currently at the reached amount
            if (quest.getUserCount(killer.getUniqueId()) <= quest.requiredAmount()) {
                userData.countKill(quest.questIdentifier(), killed);
                killer.sendRichMessage("<green>");
            }
        }
    }
}

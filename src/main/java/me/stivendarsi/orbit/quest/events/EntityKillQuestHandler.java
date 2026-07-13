package me.stivendarsi.orbit.quest.events;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class EntityKillQuestHandler implements Listener {
    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof Player killer)) return;

        LocalUserData userData = mainHandler().userHandler().getUser(killer.getUniqueId());
        Preconditions.checkNotNull(userData);


        Entity entity = event.getEntity();
        EntityType entityType = event.getEntity().getType();
        System.out.println(entityType);

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            update(questData, userData, event.getEntity());
        }
        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        for (QuestData questData : orbitData.seasonQuests()) {
            update(questData, userData, entity);
        }

    }


    private void update(QuestData questData, LocalUserData userData, Entity killedEntity) {
        if (questData == null || questData.questType() != QuestType.KILL_ENTITY) return;


        boolean entityTypeIsAllowedToKill = questData.allowedEntities().contains(killedEntity.getType());
        boolean entityKilledAlready = userData.getKilledEntities(questData.questIdentifier()).contains(killedEntity.getUniqueId());

        if (!entityTypeIsAllowedToKill || entityKilledAlready) {
            System.out.println("Entity allowed: " + killedEntity.getType());
            System.out.println("Entity killed already: " + entityKilledAlready);
            return;
        }


        questData.updateAndCheck(userData.userUUID(), 1);

        if (questData.getUserCount(userData.userUUID()) <= questData.requiredAmount()) {
            userData.countKill(questData.questIdentifier(), killedEntity.getUniqueId());
        }

    }
}

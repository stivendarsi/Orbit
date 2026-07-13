package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemType;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class FishQuestHandler implements Listener {
    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.isCancelled()) return;
        Player cause = event.getPlayer();

        Entity fish = event.getCaught();
        if (fish == null) return;
        Item itemStack = (Item) fish;
        ItemType itemType = itemStack.getItemStack().getType().asItemType();

        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            update(questData, cause.getUniqueId(), itemType);
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        for (QuestData questData : orbitData.seasonQuests()) {
            update(questData, cause.getUniqueId(), itemType);
        }
    }

    private void update(QuestData questData, UUID uuid, ItemType itemType) {
        if (questData == null || questData.questType() != QuestType.FISHING) return;

        boolean itemTypeIsAllowedToFish = questData.allowedItems().contains(itemType);

        if (!itemTypeIsAllowedToFish) {
            System.out.println("ItemType: " + itemType.getKey().asString());
            return;
        }
        questData.updateAndCheck(uuid, 1);
    }
}

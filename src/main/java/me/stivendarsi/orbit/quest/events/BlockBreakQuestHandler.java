package me.stivendarsi.orbit.quest.events;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.quest.Quest;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class BlockBreakQuestHandler implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player cause = event.getPlayer();

        LocalUserData userData = mainHandler().userHandler().getUser(cause.getUniqueId());
        Preconditions.checkNotNull(userData);


        BlockType blockType = event.getBlock().getType().asBlockType();

        for (Quest quest : mainHandler().questHandler().dailyQuests()) {
            if (quest == null || quest.questType() != QuestType.BREAK_BLOCK) continue;

            boolean blockTypeIsAllowedToBreak = quest.allowedBlockes().contains(blockType);

            if (!blockTypeIsAllowedToBreak) {
                System.out.println("BlockType: " + blockType);
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

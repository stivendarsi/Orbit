package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.quest.QuestData;
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

        BlockType blockType = event.getBlock().getType().asBlockType();


        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            if (questData == null || questData.questType() != QuestType.BREAK_BLOCK) continue;

            boolean blockTypeIsAllowedToBreak = questData.allowedBlocks().contains(blockType);

            if (!blockTypeIsAllowedToBreak) {
                System.out.println("BlockType: " + blockType.getKey());
                continue;
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

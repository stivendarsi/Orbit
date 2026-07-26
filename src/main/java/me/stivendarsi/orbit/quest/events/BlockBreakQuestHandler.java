package me.stivendarsi.orbit.quest.events;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestListMode;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class BlockBreakQuestHandler implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player cause = event.getPlayer();

        BlockType blockType = event.getBlock().getType().asBlockType();


        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
            update(questData, blockType, cause.getUniqueId());
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        for (QuestData questData : orbitData.seasonQuests()) {
            update(questData, blockType, cause.getUniqueId());
        }
    }

    private void update(QuestData questData, BlockType blockType, UUID breaker){
        if (questData == null || questData.questType() != QuestType.BREAK_BLOCK) return;

        boolean blockTypeIsAllowedToBreak;

        if (questData.questListMod() == QuestListMode.WHITE_LIST) blockTypeIsAllowedToBreak = questData.allowedBlocks().contains(blockType);
        else blockTypeIsAllowedToBreak = !questData.allowedBlocks().contains(blockType);

        if (!blockTypeIsAllowedToBreak) return;

        questData.updateAndCheck(breaker, 1);
    }
}

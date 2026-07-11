package me.stivendarsi.orbit.quest;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import me.stivendarsi.orbit.quest.enums.QuestListMode;
import me.stivendarsi.orbit.quest.enums.QuestType;
import me.stivendarsi.orbit.redis.RedisHandler;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class QuestData {
    private final QuestType questType;
    private final String questIdentifier;
    private final ItemStack questIcon;
    private final QuestAppearType appearType;
    private final QuestListMode questListMod;
    private final int descriptionWidth;
    private final String description;
    private final int requiredAmount;
    private final String rewardDescription;
    private final String rewardCommand;
    private final Map<UUID, Integer> completed;

    private final List<BlockType> allowedBlocks;
    private final List<EntityType> allowedEntities;

    public QuestData(String identifier, ConfigurationSection questSection) {
        this.completed = new HashMap<>();
        this.questIdentifier = identifier;

        String itemTypeString = questSection.getString("icon.type", "bedrock");
        this.questIcon = Registry.ITEM.get(Key.key(itemTypeString)).createItemStack();

        boolean enchanted = questSection.getBoolean("icon.enchanted");
        this.questIcon.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchanted);

        this.appearType = QuestAppearType.valueOf(questSection.getString("appears", "").toUpperCase());
        this.questType = QuestType.valueOf(questSection.getString("type", "").toUpperCase());
        this.questListMod = QuestListMode.valueOf(questSection.getString("mode", "").toUpperCase());

        this.allowedBlocks = new ArrayList<>();
        this.allowedEntities = new ArrayList<>();

        switch (this.questType) {
            case KILL_ENTITY, FISHING -> {
                List<String> allowedEntities = questSection.getStringList("allowed-entities");
                allowedEntities.forEach(s -> this.allowedEntities.add(EntityType.valueOf(s.toUpperCase())));
            }
            case BREAK_BLOCK -> {
                List<String> allowedBlocks = questSection.getStringList("allowed-blocks");
                allowedBlocks.forEach(s -> {
                    BlockType blockType = Registry.BLOCK.get(Key.key(s.toLowerCase(Locale.ROOT)));
                    this.allowedBlocks.add(blockType);
                });
            }
        }

        this.descriptionWidth = questSection.getInt("description-width");
        this.requiredAmount = questSection.getInt("required-amount");

        this.description = questSection.getString("description");
        this.rewardDescription = questSection.getString("reward-description");
        this.rewardCommand = questSection.getString("reward-command");
    }


    public void countUser(UUID uuid, int amount) {
        this.completed.put(uuid, getUserCount(uuid) + amount);
    }

    public int getUserCount(UUID uuid) {
        return this.completed.getOrDefault(uuid, 0);
    }

    public void loadUserQuestData(UUID uuid) {
        String key = RedisHandler.getQuestDataPath(questIdentifier, this.appearType);
        String amountString = mainHandler().redisClient().getSync().hget(key, String.valueOf(uuid));

        int amount = NumberUtils.toInt(amountString, 0);
        this.completed.put(uuid, amount);
    }

    public void saveUserQuestData(UUID uuid) {
        if (!this.completed.containsKey(uuid)) return;
        String key = RedisHandler.getQuestDataPath(questIdentifier, this.appearType);

        mainHandler().redisClient().getSync().hset(key, String.valueOf(uuid), String.valueOf(getUserCount(uuid)));
    }

    public QuestType questType() {
        return questType;
    }


    public QuestAppearType appearType() {
        return appearType;
    }

    public int descriptionWidth() {
        return descriptionWidth;
    }

    public String description() {
        return description;
    }

    public int requiredAmount() {
        return requiredAmount;
    }

    public String rewardDescription() {
        return rewardDescription;
    }

    public String rewardCommand() {
        return rewardCommand;
    }

    public ItemStack questIcon() {
        return questIcon;
    }

    public List<EntityType> allowedEntities() {
        System.out.println(this.allowedEntities);
        return allowedEntities;
    }

    public List<BlockType> allowedBlocks() {
        System.out.println("blocks:" + this.allowedBlocks);
        return allowedBlocks;
    }

    public String questIdentifier() {
        return questIdentifier;
    }
}

package me.stivendarsi.orbit.quest;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import me.stivendarsi.orbit.quest.enums.QuestListMode;
import me.stivendarsi.orbit.quest.enums.QuestType;
import me.stivendarsi.orbit.quest.events.PlayTimeQuestHandler;
import me.stivendarsi.orbit.redis.RedisHandler;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

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

    private final Map<UUID, Integer> completedCounter;

    private final List<BlockType> allowedBlocks;
    private final List<EntityType> allowedEntities;
    private final List<ItemType> allowedItems;

    public QuestData(@NotNull String identifier, @NotNull ConfigurationSection questSection) {
        this.completedCounter = new HashMap<>();
        this.questIdentifier = identifier;

        String itemTypeString = questSection.getString("icon.type", "bedrock");
        this.questIcon = Registry.ITEM.get(Key.key(itemTypeString)).createItemStack();

        boolean enchanted = questSection.getBoolean("icon.enchanted");
        this.questIcon.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchanted);

        this.appearType = QuestAppearType.valueOf(questSection.getString("appears", "").toLowerCase());
        this.questType = QuestType.valueOf(questSection.getString("type", "").toUpperCase());
        this.questListMod = QuestListMode.valueOf(questSection.getString("mode", "").toUpperCase());

        this.allowedBlocks = new ArrayList<>();
        this.allowedEntities = new ArrayList<>();
        this.allowedItems = new ArrayList<>();

        switch (this.questType) {
            case KILL_ENTITY -> {
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
            case FISHING -> {
                List<String> allowedItems = questSection.getStringList("allowed-items");
                allowedItems.forEach(s -> {
                    ItemType itemType = Registry.ITEM.get(Key.key(s.toLowerCase(Locale.ROOT)));
                    this.allowedItems.add(itemType);
                });
            }
        }

        this.descriptionWidth = questSection.getInt("description-width");
        this.requiredAmount = questSection.getInt("required-amount");

        this.description = questSection.getString("description");
        this.rewardDescription = questSection.getString("reward-description");
        this.rewardCommand = questSection.getString("reward-command");
    }

    public QuestListMode questListMod() {
        return questListMod;
    }

    public List<ItemType> allowedItems() {
        return allowedItems;
    }

    public void updateAndCheck(UUID uuid, int amountToCount) {
        countUser(uuid, amountToCount);

        boolean rewardPlayer = getUserCount(uuid) == this.requiredAmount;

        if (rewardPlayer) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) orbitInstance().getLogger().warning("Null player");
            Constants.runCommandInConsole(player, this.rewardCommand); // Reward the user if he is currently at the reached amount
        }
    }

    public void resetCounter(){
        this.completedCounter.clear();
    }

    public void removeUser(UUID uuid){
        this.completedCounter.remove(uuid);
    }

    public void countUser(UUID uuid, int amount) {
        this.completedCounter.put(uuid, getUserCount(uuid) + amount);
    }

    public int getUserCount(UUID uuid) {
        return this.completedCounter.getOrDefault(uuid, 0);
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
        return allowedEntities;
    }

    public List<BlockType> allowedBlocks() {
        return allowedBlocks;
    }

    public String questIdentifier() {
        return questIdentifier;
    }

    public Duration currentSessionTimePlayed(UUID userUUID) {
        Duration played = Duration.ofMillis(System.currentTimeMillis() - PlayTimeQuestHandler.playTime().getOrDefault(userUUID, System.currentTimeMillis()));
        return Duration.ofDays(played.toDays())
                .plusHours(played.toHoursPart())
                .plusMinutes(played.toMinutesPart());
    }
}

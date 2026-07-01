package me.stivendarsi.orbit.experience;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.stivendarsi.orbit.redis.RedisHandler;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class Quest {
    private final String questIdentifier;
    private final ItemStack questIcon;
    private final APPEAR_TYPE appearType;
    private final int descriptionWidth;
    private final String description;
    private final int requiredAmount;
    private final String rewardDescription;
    private final String rewardCommand;
    private final Map<UUID, Integer> completed;

    public Quest(String identifier, ConfigurationSection questSection) {
        this.completed = new HashMap<>();
        this.questIdentifier = identifier;

        String itemTypeString = questSection.getString("icon.type", "bedrock");
        this.questIcon = Registry.ITEM.get(Key.key(itemTypeString)).createItemStack();

        boolean enchanted = questSection.getBoolean("icon.enchanted");
        this.questIcon.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchanted);

        this.appearType = APPEAR_TYPE.valueOf(questSection.getString("appears"));
        this.descriptionWidth = questSection.getInt("description-width");
        this.requiredAmount = questSection.getInt("required-amount");

        this.description = questSection.getString("description");
        this.rewardDescription = questSection.getString("reward-description");
        this.rewardCommand = questSection.getString("reward-command");
    }


    public void countUser(UUID uuid, int amount) {
        this.completed.put(uuid, getCount(uuid) + amount);
    }

    public int getCount(UUID uuid) {
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

        mainHandler().redisClient().getSync().hset(key, String.valueOf(uuid), String.valueOf(getCount(uuid)));
    }

    public void saveAllQuestData() {
        String key;
        if (this.appearType == APPEAR_TYPE.DAILY) key = "orbit:quest_data:daily:%s".formatted(this.questIdentifier);
        else key = "orbit:quest_data:season:%s".formatted(questIdentifier);

        Map<String, String> data = new HashMap<>();

        this.completed.forEach((uuid, amount) -> {
            data.put(String.valueOf(uuid), String.valueOf(amount));
        });

        mainHandler().redisClient().getSync().hset(key, data);
    }

    public enum APPEAR_TYPE {
        SEASON,
        DAILY
    }

    public APPEAR_TYPE appearType() {
        return appearType;
    }

    public String identifier() {
        return questIdentifier;
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
}

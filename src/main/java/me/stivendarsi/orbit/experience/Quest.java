package me.stivendarsi.orbit.experience;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Quest {
    private final String identifier;
    private final ItemStack questIcon;
    private final APPEAR_TYPE appearType;
    private final int descriptionWidth;
    private final String description;
    private final int requiredAmount;
    private final String rewardDescription;
    private final String rewardCommand;

    public Quest(String identifier, ConfigurationSection questSection) {
        this.identifier = identifier;

        String itemTypeString = questSection.getString("itemstack.type", "bedrock");
        this.questIcon = Registry.ITEM.get(Key.key(itemTypeString)).createItemStack();

        boolean enchanted = questSection.getBoolean("itemstack.enchanted");
        this.questIcon.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchanted);

        this.appearType = APPEAR_TYPE.valueOf(questSection.getString("appears"));
        this.descriptionWidth = questSection.getInt("description-width");
        this.requiredAmount = questSection.getInt("required-amount");

        this.description = questSection.getString("description");
        this.rewardDescription = questSection.getString("reward-description");
        this.rewardCommand = questSection.getString("reward-command");
    }

    public enum APPEAR_TYPE {
        SEASON,
        DAILY
    }

    public APPEAR_TYPE appearType() {
        return appearType;
    }

    public String identifier() {
        return identifier;
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

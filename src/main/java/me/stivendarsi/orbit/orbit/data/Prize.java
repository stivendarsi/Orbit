package me.stivendarsi.orbit.orbit.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

import static me.stivendarsi.orbit.Orbit.plugin;

public class Prize {
    private final int levelIndex;
    private final boolean plus;
    private final List<String> description;
    private final String iconReward;
    private final String rewardCommand;

    public Prize(int prizeIndex, boolean plus, ConfigurationSection section) {
        this.levelIndex = prizeIndex;
        this.plus = plus;
        this.description = section.getStringList("description");
        this.iconReward = section.getString("icon-reward");
        this.rewardCommand = section.getString("icon-command");
    }

    public int prizeIndex() {
        return levelIndex;
    }

    public boolean plus() {
        return plus;
    }

    public List<String> description() {
        return description;
    }

    public String iconReward() {
        return iconReward;
    }

    public void claimReward(Player claimingUser) {
        plugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), this.rewardCommand.replace("<player_name>", claimingUser.getName()));
    }
}

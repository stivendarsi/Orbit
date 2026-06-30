package me.stivendarsi.orbit.experience;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static me.stivendarsi.orbit.Orbit.plugin;

public class Quest {
    private final String orbitIdentifier;
    private final String rewardCommand;
    private final int requiredAmount;
    private final String questIdentifier;
    private final List<String> description;

    public Quest(String orbitIdentifier, String rewardCommand, int requiredAmount, String questIdentifier, List<String> description) {
        this.orbitIdentifier = orbitIdentifier;
        this.rewardCommand = rewardCommand;
        this.requiredAmount = requiredAmount;
        this.questIdentifier = questIdentifier;
        this.description = description;
    }

    public void claimReward(Player claimingUser) {
        if (this.rewardCommand == null) return;
        plugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), this.rewardCommand.replace("<player_name>", claimingUser.getName()));
    }

    public int requiredAmount() {
        return requiredAmount;
    }

    public String rewardCommand() {
        return rewardCommand;
    }

    public String questIdentifier() {
        return questIdentifier;
    }

    public List<String> description() {
        return description;
    }

    public String orbitIdentifier() {
        return orbitIdentifier;
    }
}

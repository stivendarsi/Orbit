package me.stivendarsi.orbit.experience;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static me.stivendarsi.orbit.Orbit.plugin;

public class Quest {
    private final String commandToRun;

    public Quest(String rewardCommand) {
        this.commandToRun = rewardCommand;
    }

    public void reward() {
        CommandSender sender = Bukkit.createCommandSender(_ -> {
        });
        boolean b = plugin().getServer().dispatchCommand(sender, this.commandToRun);
        System.out.println(b);
    }
}

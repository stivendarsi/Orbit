package me.stivendarsi.orbit;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class Constants {
    public static final Sound clickSound = Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.UI, 1,1);
    public static final String unLocked = "\uD83D\uDD13";
    public static final String locked = "\uD83D\uDD12";
    public static void runCommandInConsole(Player claimingUser,@Nullable String rewardCommand) {
        if (rewardCommand == null) {
            System.out.println("Null command");
            return;
        }
        rewardCommand = rewardCommand.replace("<player_name>", claimingUser.getName());
        System.out.println("Command: " + rewardCommand);
        orbitInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
    }
}

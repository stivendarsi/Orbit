package me.stivendarsi.orbit;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class Constants {
    public static final Sound clickSound = Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.UI, 1, 1);
    public static final Sound pingSound = Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.UI, 1, 1);

    public static boolean runCommandInConsole(Player claimingUser, @Nullable String rewardCommand) {
        if (rewardCommand == null) return false;
        rewardCommand = rewardCommand.replace("<player_name>", claimingUser.getName());
        orbitInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
        return true;
    }
}

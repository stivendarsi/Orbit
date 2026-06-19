package me.stivendarsi.orbit.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.orbit.orbit.MainMenu;
import me.stivendarsi.orbit.orbit.OrbitMenu;
import org.bukkit.entity.Player;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class OrbitCommands {
    public static int open(CommandContext<CommandSourceStack> context) {
        int userExperience = context.getArgument("experience", Integer.class);

        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        MainMenu mainMenu = new MainMenu(userExperience);
        mainMenu.openMainMenu(player);
        return 1;
    }


    public static int experience(CommandContext<CommandSourceStack> context) {
        int amount = context.getArgument("amount", Integer.class);

        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        mainHandler().experienceHandler().modifyUserExperience(player.getUniqueId(), amount);
        return 1;
    }

}
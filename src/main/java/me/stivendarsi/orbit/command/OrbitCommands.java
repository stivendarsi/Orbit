package me.stivendarsi.orbit.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.orbit.orbit.OrbitMenu;
import org.bukkit.entity.Player;

public class OrbitCommands {
    public static int open(CommandContext<CommandSourceStack> context) {

        int userExperience = context.getArgument("experience", Integer.class);

        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        OrbitMenu orbit = new OrbitMenu(userExperience);
        orbit.openOrbit(player);
        return 1;
    }
}
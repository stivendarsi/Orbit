package me.stivendarsi.orbit.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class Reload {
    public static int reload(CommandContext<CommandSourceStack> context) {
        orbitInstance().reloadConfig();
        orbitInstance().getServer().getAsyncScheduler().cancelTasks(orbitInstance());


        mainHandler().unLoad(context.getSource().getSender());

        mainHandler().load(context.getSource().getSender());

        context.getSource().getSender().sendMessage(Component.text("נטען מחדש!", NamedTextColor.GREEN));
        return 1;
    }
}
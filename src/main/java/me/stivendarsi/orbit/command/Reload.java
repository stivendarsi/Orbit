package me.stivendarsi.orbit.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.plugin;

public class Reload {
    public static int reload(CommandContext<CommandSourceStack> context) {
        plugin().reloadConfig();
        plugin().getServer().getAsyncScheduler().cancelTasks(plugin());
        mainHandler().load();
        context.getSource().getSender().sendMessage(Component.text("נטען מחדש!", NamedTextColor.GREEN));
        return 1;
    }
}
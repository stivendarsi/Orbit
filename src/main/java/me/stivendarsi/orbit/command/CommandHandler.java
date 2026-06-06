package me.stivendarsi.orbit.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;


public class CommandHandler {
    public static void register(LifecycleEventManager<@NotNull Plugin> manager) {
        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();
            commands.register(Commands.literal("orbit").requires(source -> source.getSender().hasPermission("orbit.admin"))
                            .then(Commands.argument("experience", integer(0)).executes(OrbitCommands::open))
                    .build());
        });

    }
}

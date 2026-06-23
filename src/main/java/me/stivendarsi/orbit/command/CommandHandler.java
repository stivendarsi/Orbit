package me.stivendarsi.orbit.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.player;


public class CommandHandler {
    public static void register(LifecycleEventManager<@NotNull Plugin> manager) {
        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();
            commands.register(Commands.literal("experience").requires(source -> source.getSender().hasPermission("orbit.admin"))
                    .then(Commands.argument("target", player())
                            .then(Commands.literal("get").executes(OrbitCommands::getExperience))
                            .then(Commands.literal("reset").executes(OrbitCommands::resetExperience))
                            .then(Commands.literal("modify")
                                    .then(Commands.argument("amount", integer()).executes(OrbitCommands::modifyExperience))
                            )
                            .then(Commands.literal("set")
                                    .then(Commands.argument("amount", integer(0)).executes(OrbitCommands::setExperience))
                            )
                    )
                    .build());
            commands.register(Commands.literal("orbit").requires(source -> source.getSender().hasPermission("orbit.admin"))
                    .executes(OrbitCommands::open)
                    .build());
        });

    }
}

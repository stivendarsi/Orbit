package me.stivendarsi.orbit.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.stivendarsi.orbit.experience.Quest;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.player;
import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;


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

            commands.register(Commands.literal("quests")
                    .then(Commands.argument("quest-id", word()).executes(OrbitCommands::getQuestData)
                            .suggests((context, builder) -> {
                                        for (Quest quest : mainHandler().questHandler().dailyQuests()) {
                                            builder.suggest(quest.questIdentifier());
                                        }
                                        return builder.buildFuture();
                                    }
                            )
                    ).build());
            commands.register(Commands.literal("orbit").requires(source -> source.getSender().hasPermission("orbit.admin"))
                    .executes(OrbitCommands::open)
                            .then(Commands.literal("reload").executes(context -> {
                                orbitInstance().reloadConfig();
                                mainHandler().load();
                                context.getSource().getSender().sendRichMessage("<green>נטען מחדש");
                                return 1;
                            }))
                    .build());
        });
    }
}

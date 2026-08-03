package me.stivendarsi.orbit.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.stivendarsi.orbit.quest.QuestData;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.player;
import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;


public class CommandHandler {
    public static void register(LifecycleEventManager<@NotNull Plugin> manager) {
        Permission orbitAdmin = new Permission("orbit.admin");
        orbitInstance().getServer().getPluginManager().addPermission(orbitAdmin);

        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();
            commands.register(Commands.literal("experience").requires(source -> source.getSender().hasPermission(orbitAdmin))
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

            commands.register(Commands.literal("quest-debug").requires(source -> source.getSender().hasPermission(orbitAdmin))
                    .then(Commands.argument("quest-id", word()).executes(OrbitCommands::getQuestData)
                            .suggests((context, builder) -> {
                                        for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
                                            builder.suggest(questData.questIdentifier());
                                        }
                                        return builder.buildFuture();
                                    }
                            )
                    ).then(Commands.literal("timer")
                            .then(Commands.literal("now").executes(context -> {
                                        context.getSource().getSender().sendRichMessage(mainHandler().questHandler().getCurrentTime().format(DateTimeFormatter.ISO_DATE_TIME));
                                        return 1;
                                    }
                            ))
                            .then(Commands.literal("time-left").executes(context -> {
                                        context.getSource().getSender().sendRichMessage(mainHandler().questHandler().timeLeftAsString());
                                        return 1;
                                    }
                            ))
                            .then(Commands.literal("next").executes(context -> {
                                        context.getSource().getSender().sendRichMessage(mainHandler().questHandler().getNextQuestTime().format(DateTimeFormatter.ISO_DATE_TIME));
                                        return 1;
                                    }
                            ))
                    )

                    .build()
            );

            commands.register(Commands.literal("orbit")
                    .then(Commands.literal("give").requires(source -> source.getSender().hasPermission(orbitAdmin))
                            .then(Commands.argument("player_name", word())
                                    .then(Commands.argument("orbit_identifier", string()).executes(OrbitCommands::giveOrbit).suggests((context, builder) -> {
                                                for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
                                                    builder.suggest(orbitIdentifier);
                                                }
                                                return builder.buildFuture();
                                            })
                                    )
                            )
                    )

                    .executes(OrbitCommands::open)
                    .then(Commands.literal("reload").requires(source -> source.getSender().hasPermission(orbitAdmin)).executes(context -> {

                        mainHandler().unLoad();

                        orbitInstance().reloadConfig();
                        mainHandler().load();
                        context.getSource().getSender().sendRichMessage("<green>נטען מחדש");
                        return 1;
                    }))
                    .build());
        });
    }
}

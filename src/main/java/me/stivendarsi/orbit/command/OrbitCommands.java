package me.stivendarsi.orbit.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.stivendarsi.orbit.orbit.MainMenu;
import org.bukkit.entity.Player;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class OrbitCommands {
    public static int open(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        MainMenu mainMenu = new MainMenu(mainHandler().experienceHandler().getExperience(player.getUniqueId()));
        mainMenu.openMainMenu(player);
        return 1;
    }

    public static int setExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        int amount = context.getArgument("amount", Integer.class);

        mainHandler().experienceHandler().setUserExperience(target.getUniqueId(), amount);
        return 1;
    }


    public static int modifyExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        int amount = context.getArgument("amount", Integer.class);

        mainHandler().experienceHandler().modifyUserExperience(target.getUniqueId(), amount);
        return 1;
    }
    public static int resetExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        mainHandler().experienceHandler().setUserExperience(target.getUniqueId(), 0);
        return 1;
    }

    public static int getExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(ctx.getSource()).getFirst();

        int amount = mainHandler().experienceHandler().getExperience(target.getUniqueId());
        ctx.getSource().getSender().sendRichMessage("Target experience: " + amount);
        return 1;
    }

}
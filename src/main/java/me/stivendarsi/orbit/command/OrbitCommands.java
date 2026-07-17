package me.stivendarsi.orbit.command;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.menus.MainMenu;
import org.bukkit.entity.Player;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class OrbitCommands {
    public static int open(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        MainMenu.openMainMenu(player);
        return 1;
    }

    public static int setExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        int amount = context.getArgument("amount", Integer.class);

        LocalUserData localUserData = mainHandler().userHandler().getUser(target.getUniqueId());
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        localUserData.setUserOrbitExperience(orbitData.identifier(), amount);

        return 1;
    }


    public static int modifyExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        int amount = context.getArgument("amount", Integer.class);


        LocalUserData localUserData = mainHandler().userHandler().getUser(target.getUniqueId());
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        localUserData.modifyUserExperience(orbitData.identifier(), amount);
        target.sendRichMessage("<cut_progress_pink:'קיבלת %s⭐ כוכבים!'>".formatted(amount), MiniPlaceholders.audienceGlobalPlaceholders());
        return 1;
    }

    public static int resetExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        LocalUserData localUserData = mainHandler().userHandler().getUser(target.getUniqueId());
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        localUserData.setUserOrbitExperience(orbitData.identifier(), 0);
        return 1;
    }

    public static int getExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(ctx.getSource()).getFirst();

        LocalUserData localUserData = mainHandler().userHandler().getUser(target.getUniqueId());
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        int amount = localUserData.getUserExperience(orbitData.identifier());
        ctx.getSource().getSender().sendRichMessage("Target experience: " + amount);
        return 1;
    }

    public static int getQuestData(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String questIdentifier = ctx.getArgument("quest-id", String.class);
        QuestData questData = mainHandler().questHandler().getQuestData(questIdentifier);
        if (questData == null) return 0;
        ctx.getSource().getSender().sendRichMessage("הושלם: " + questData.getUserCount(ctx.getSource().getExecutor().getUniqueId()));
        return 1;
    }

}
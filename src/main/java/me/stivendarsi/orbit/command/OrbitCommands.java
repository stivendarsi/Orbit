package me.stivendarsi.orbit.command;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.menus.MainMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.luckPerms;
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

    public static int giveOrbit(CommandContext<CommandSourceStack> ctx){
        String orbitIdentifier = ctx.getArgument("orbit_identifier", String.class);
        String playerName = ctx.getArgument("player_name", String.class);

        CommandSender sender = ctx.getSource().getSender();

        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
        if (orbitData == null) {
            sender.sendRichMessage("<red>מסלול התקדמות זה לא קיים.</red>");
            return 0;
        }

        Component playerNotFound = Constants.color("<red>משתמש לא נמצא.</red>");
        Component orbitDoesntHavePermission = Constants.color("<red>אין גישה למסלול התקדמות זה.</red>");

        UUID userUuid = Bukkit.getPlayerUniqueId(playerName);
        if (userUuid == null) {
            sender.sendMessage(playerNotFound);
            return 0;
        }

        User user = luckPerms().getUserManager().getUser(userUuid);

        if (user == null) {
            sender.sendMessage(playerNotFound);
            return 0;
        }

        Permission orbitPermission = mainHandler().orbitHandler().getOrbitPermission(orbitIdentifier);

        if (orbitPermission == null) {
            sender.sendMessage(orbitDoesntHavePermission);
            return 0;
        }

        Node node = Node.builder(orbitPermission.getName()).build();
        user.data().add(node);
        luckPerms().getUserManager().saveUser(user);

        Player player = Bukkit.getPlayer(userUuid);
        if (player != null) {
            TagResolver resolver = TagResolver.builder().tag("orbit_title", Tag.preProcessParsed(orbitData.title())).build();
            Component receivedOrbitPermissionMessage = Constants.color(player, "<cut_progress_pink:'קיבלת גישה למסלול התקדמות: <orbit_title>'>", resolver);
            player.sendMessage(receivedOrbitPermissionMessage);
        }
        return 1;
    }
}
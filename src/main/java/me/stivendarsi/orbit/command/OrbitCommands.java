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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.*;

public class OrbitCommands {
    public static int open(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        Bukkit.getAsyncScheduler().runNow(orbitInstance(), scheduledTask -> {
            MainMenu.openMainMenu(player);
        });
        return 1;
    }

    public static int setExperience(CommandContext<CommandSourceStack> context) {
        final String playerName = context.getArgument("player_name", String.class);
        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);
        if (userUUID == null) {
            context.getSource().getSender().sendRichMessage("<red>שחקן לא נמצא");
            return 0;
        }
        int amount = context.getArgument("amount", Integer.class);

        LocalUserData localUserData = mainHandler().userHandler().getUser(userUUID);
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        localUserData.setUserOrbitExperience(orbitData.identifier(), amount);
        Player user = Bukkit.getPlayer(userUUID);
        if (user != null)
            user.sendRichMessage("<cut_progress_pink:'הוגדרו לך %s⭐ כוכבים!'>".formatted(amount), MiniPlaceholders.audienceGlobalPlaceholders());
        return 1;
    }


    public static int modifyExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final String playerName = context.getArgument("player_name", String.class);
        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);
        if (userUUID == null) {
            context.getSource().getSender().sendRichMessage("<red>שחקן לא נמצא");
            return 0;
        }

        int amount = context.getArgument("amount", Integer.class);

        LocalUserData localUserData = mainHandler().userHandler().getUser(userUUID);
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        localUserData.modifyUserExperience(orbitData.identifier(), amount);
        Player user = Bukkit.getPlayer(userUUID);
        if (user != null)
            user.sendRichMessage("<cut_progress_pink:'קיבלת %s⭐ כוכבים!'>".formatted(amount), MiniPlaceholders.audienceGlobalPlaceholders());
        return 1;
    }

    public static int resetExperience(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final String playerName = context.getArgument("player_name", String.class);
        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);
        if (userUUID == null) {
            context.getSource().getSender().sendRichMessage("<red>שחקן לא נמצא");
            return 0;
        }

        LocalUserData localUserData = mainHandler().userHandler().getUser(userUUID);
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        localUserData.setUserOrbitExperience(orbitData.identifier(), 0);
        Player user = Bukkit.getPlayer(userUUID);
        if (user != null)
            user.sendRichMessage("<cut_progress_pink:'אופסו לך כוכבים!'>", MiniPlaceholders.audienceGlobalPlaceholders());
        return 1;
    }

    public static int getExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final String playerName = ctx.getArgument("player_name", String.class);
        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);

        if (userUUID == null) {
            ctx.getSource().getSender().sendRichMessage("<red>שחקן לא נמצא");
            return 0;
        }

        LocalUserData localUserData = mainHandler().userHandler().getUser(userUUID);
        Preconditions.checkNotNull(localUserData, "Null user data");

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");

        int amount = localUserData.getUserExperience(orbitData.identifier());
        ctx.getSource().getSender().sendRichMessage("<cut_progress_pink:'לשחקן %s יש %s⭐ כוכבים'>".formatted(playerName, amount), MiniPlaceholders.audienceGlobalPlaceholders());
        return 1;
    }

    public static int getQuestData(CommandContext<CommandSourceStack> ctx) {
        final String playerName = ctx.getArgument("player_name", String.class);
        UUID userUUID = Bukkit.getPlayerUniqueId(playerName);

        if (userUUID == null) {
            ctx.getSource().getSender().sendRichMessage("<red>שחקן לא נמצא");
            return 0;
        }
        String questIdentifier = ctx.getArgument("quest-id", String.class);
        QuestData questData = mainHandler().questHandler().getQuestData(questIdentifier);
        if (questData == null) return 0;


        String msg = "<#fffb00>השחקן " + playerName + " השלים " + NumberFormat.getNumberInstance().format(questData.getUserProgress(userUUID)) + " מתוך " + NumberFormat.getNumberInstance().format(questData.requiredAmount()) + "</#fffb00>";

        Component msgComponent = Constants.color(ctx.getSource().getSender(), msg);
        ctx.getSource().getSender().sendMessage(msgComponent);
        return 1;
    }

    public static int giveOrbit(CommandContext<CommandSourceStack> ctx) {
        String orbitIdentifier = ctx.getArgument("orbit_identifier", String.class);
        String playerName = ctx.getArgument("player_name", String.class);

        CommandSender sender = ctx.getSource().getSender();

        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
        if (orbitData == null) {
            sender.sendRichMessage("<red>מסלול התקדמות זה לא קיים!, תנתן גישה מיותרת!</red>");
        }

        Component playerNotFound = Constants.color("<red>משתמש לא נמצא.</red>");

        UUID userUuid = Bukkit.getPlayerUniqueId(playerName);
        if (userUuid == null) {
            sender.sendMessage(playerNotFound);
            return 0;
        }

        User user = luckPerms().getUserManager().getUser(userUuid);

        if (user != null) return handleUser(user, orbitIdentifier, playerName, userUuid, orbitData);
        luckPerms().getUserManager().loadUser(userUuid).thenAccept(loadedUser -> {
            handleUser(loadedUser, orbitIdentifier, playerName, userUuid, orbitData);
        });

        return 1;
    }

    private static int handleUser(@NotNull User user, String orbitIdentifier, String playerName, UUID userUuid, OrbitData orbitData) {
        Permission orbitPermission = new Permission("orbit.access." + orbitIdentifier, PermissionDefault.FALSE);

        Node node = Node.builder(orbitPermission.getName()).build();
        user.data().add(node);
        luckPerms().getUserManager().saveUser(user);

        orbitInstance().getLogger().warning("Gave orbit permission to " + playerName + ": " + orbitPermission.getName());

        Player player = Bukkit.getPlayer(userUuid);
        if (player != null) {
            String title = "<gold>ייצא בעתיד</gold>";
            if (orbitData != null) title = orbitData.title();
            TagResolver resolver = TagResolver.builder().tag("orbit_title", Tag.preProcessParsed(title)).build();
            Component receivedOrbitPermissionMessage = Constants.color(player, "<cut_progress_pink:'קיבלת גישה למסלול התקדמות:'> <orbit_title>", resolver);
            player.sendMessage(receivedOrbitPermissionMessage);
        }
        return 1;
    }
}
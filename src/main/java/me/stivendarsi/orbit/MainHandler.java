package me.stivendarsi.orbit;

import me.stivendarsi.orbit.orbit.menus.QuestMenu;
import me.stivendarsi.orbit.quest.QuestHandler;
import me.stivendarsi.orbit.message.MessagesHandler;
import me.stivendarsi.orbit.orbit.OrbitHandler;
import me.stivendarsi.orbit.orbit.UserHandler;
import me.stivendarsi.orbit.redis.RedisHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class MainHandler {

    private final RedisHandler redisClient;
    private final OrbitHandler orbitHandler;
    private final UserHandler userHandler;
    private final QuestHandler questHandler;
    private final MessagesHandler messagesHandler;

    public MainHandler() {
        this.messagesHandler = new MessagesHandler();
        redisClient = new RedisHandler();
        this.questHandler = new QuestHandler();
        orbitHandler = new OrbitHandler();
        this.userHandler = new UserHandler();
    }


    public void unLoad(Audience audience) {
        audience.sendMessage(Component.text("שומר נתונים של שחקנים...", NamedTextColor.RED));
        this.userHandler.saveAllData();

        audience.sendMessage(Component.text("מפרק גישות...", NamedTextColor.RED));
        mainHandler().orbitHandler.unLoadPermissions();
    }


    public void load(Audience audience){
        audience.sendMessage(Component.text("טוען הודעות...", NamedTextColor.YELLOW));
        this.messagesHandler.load();
        audience.sendMessage(Component.text("טוען מאגר מידע...", NamedTextColor.YELLOW));
        this.redisClient.load();
        audience.sendMessage(Component.text("טוען משימות...", NamedTextColor.YELLOW));
        this.questHandler.load();
        audience.sendMessage(Component.text("טוען מסלולי התקדמות...", NamedTextColor.YELLOW));
        this.orbitHandler.load();
        audience.sendMessage(Component.text("טוען מידע של שחקנים...", NamedTextColor.YELLOW));
        this.userHandler.load();

        audience.sendMessage(Component.text("טוען גישות...", NamedTextColor.YELLOW));
        this.orbitHandler.loadPermissions();

        audience.sendMessage(Component.text("טוען חלקים קבועים בתפריט משימות...", NamedTextColor.YELLOW));
        QuestMenu.loadStaticBlocks();
    }


    public MessagesHandler messagesHandler() {
        return messagesHandler;
    }
    public QuestHandler questHandler() {
        return questHandler;
    }

    public RedisHandler redisClient() {
        return redisClient;
    }

    public OrbitHandler orbitHandler() {
        return orbitHandler;
    }
    public UserHandler userHandler() {
        return userHandler;
    }
}

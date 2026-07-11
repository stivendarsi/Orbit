package me.stivendarsi.orbit;

import me.stivendarsi.orbit.experience.QuestHandler;
import me.stivendarsi.orbit.message.MessagesHandler;
import me.stivendarsi.orbit.orbit.OrbitHandler;
import me.stivendarsi.orbit.orbit.UserHandler;
import me.stivendarsi.orbit.redis.RedisHandler;

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


    public void load(){
        this.messagesHandler.load();
        this.redisClient.load();
        this.questHandler.load();
        this.orbitHandler.load();
        this.userHandler.load();
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

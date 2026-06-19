package me.stivendarsi.orbit;

import me.stivendarsi.orbit.redis.RedisHandler;

public class MainHandler {

    private final RedisHandler redisClient;
    private final ExperienceHandler experienceHandler;

    public MainHandler() {
        redisClient = new RedisHandler();
        experienceHandler = new ExperienceHandler();
    }

    public void load(){
        this.redisClient.load();
        this.experienceHandler.load();
    }

    public RedisHandler redisClient() {
        return redisClient;
    }

    public ExperienceHandler experienceHandler() {
        return experienceHandler;
    }
}

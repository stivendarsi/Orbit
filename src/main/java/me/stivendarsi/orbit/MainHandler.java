package me.stivendarsi.orbit;

import me.stivendarsi.orbit.experience.ExperienceHandler;
import me.stivendarsi.orbit.orbit.data.OrbitHandler;
import me.stivendarsi.orbit.redis.RedisHandler;

public class MainHandler {

    private final RedisHandler redisClient;
    private final ExperienceHandler experienceHandler;
    private final OrbitHandler orbitHandler;

    public MainHandler() {
        redisClient = new RedisHandler();
        experienceHandler = new ExperienceHandler();
        orbitHandler = new OrbitHandler();
    }

    public void load(){
        this.redisClient.load();
        this.experienceHandler.load();
        this.orbitHandler.load();
    }

    public RedisHandler redisClient() {
        return redisClient;
    }

    public ExperienceHandler experienceHandler() {
        return experienceHandler;
    }

    public OrbitHandler orbitHandler() {
        return orbitHandler;
    }
}

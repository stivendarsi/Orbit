package me.stivendarsi.orbit;

import me.stivendarsi.orbit.experience.ExperienceHandler;
import me.stivendarsi.orbit.orbit.data.OrbitHandler;
import me.stivendarsi.orbit.orbit.data.UserHandler;
import me.stivendarsi.orbit.redis.RedisHandler;

public class MainHandler {

    private final RedisHandler redisClient;
    private final OrbitHandler orbitHandler;
    private final UserHandler userHandler;

    public MainHandler() {
        redisClient = new RedisHandler();
        orbitHandler = new OrbitHandler();
        this.userHandler = new UserHandler();
    }



    public void load(){
        this.redisClient.load();
        this.orbitHandler.load();
        this.userHandler.load();
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

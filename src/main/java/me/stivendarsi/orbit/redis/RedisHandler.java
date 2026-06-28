package me.stivendarsi.orbit.redis;

import redis.clients.jedis.*;

import java.util.UUID;

public class RedisHandler {
    private RedisClient client;

    public void load() {

        this.client = RedisClient.create("82.22.24.12", 25595, "default", "A@pX#=sEk)x,luo5U5jx^G)Y&Vmi&oDGOr[&^Bly");
    }



    public String getUserDataPath(String orbitIdentifier,UUID userUUID, DataType dataType){
        return "orbit:orbit_data:" + orbitIdentifier + ":" + userUUID + ":" + dataType.toString();
    }


    public RedisClient getClient() {
        return this.client;
    }
}

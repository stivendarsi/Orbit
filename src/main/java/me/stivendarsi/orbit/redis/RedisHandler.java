package me.stivendarsi.orbit.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.UUID;

public class RedisHandler {
    private StatefulRedisConnection<String, String> connection;

    public void load() {

        RedisURI uri = RedisURI.Builder
                .redis("82.22.24.12", 25595)
                .withAuthentication("default", "A@pX#=sEk)x,luo5U5jx^G)Y&Vmi&oDGOr[&^Bly").build();

        RedisClient client = RedisClient.create(uri);
        this.connection = client.connect();

//        this.client = RedisClient.create("82.22.24.12", 25595, "default", "A@pX#=sEk)x,luo5U5jx^G)Y&Vmi&oDGOr[&^Bly");
    }

    public static String getUserDataPath(String orbitIdentifier, UUID userUUID, DataType dataType) {
        return "orbit:orbit_data:" + orbitIdentifier + ":" + userUUID + ":" + dataType.toString();
    }

    public RedisCommands<String, String> getSync(){
        return this.connection.sync();
    }

    public RedisAsyncCommands<String, String> getAsync(){
        return this.connection.async();
    }
}

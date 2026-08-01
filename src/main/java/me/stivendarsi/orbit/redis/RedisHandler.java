package me.stivendarsi.orbit.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;

import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class RedisHandler {
    private StatefulRedisConnection<String, String> connection;

    public void load() {

        RedisURI uri = RedisURI.builder().withHost("82.22.24.12").withPort(25595).withAuthentication("default","A@pX#=sEk)x,luo5U5jx^G)Y&Vmi&oDGOr[&^Bly").build();

        RedisClient client = RedisClient.create(uri);
        this.connection = client.connect();

    }

    public static String getUserDataPath(String orbitIdentifier, UUID userUUID, DataType dataType) {
        return "orbit:orbit_data:" + orbitIdentifier + ":" + userUUID + ":" + dataType.toString();
    }

    public static String getQuestDataPath(String questIdentifier, QuestAppearType appearType){
        String key;
        if (appearType == QuestAppearType.DAILY) key = "orbit:quest_data:daily:%s".formatted(questIdentifier);
        else key = "orbit:quest_data:season:%s".formatted(questIdentifier);
        return key;
    }

    public static boolean pathExists(String path){
        return 0 < mainHandler().redisClient().getSync().exists(path);
    }

    public RedisCommands<String, String> getSync(){
        return this.connection.sync();
    }

    public RedisAsyncCommands<String, String> getAsync(){
        return this.connection.async();
    }
}

package me.stivendarsi.orbit.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class RedisHandler {
    private StatefulRedisConnection<String, String> connection;
    private String parentFolder;

    public void load() {
        FileConfiguration c = orbitInstance().getConfig();
        String host = c.getString("redis.host");
        int port = c.getInt("redis.port");
        String user = c.getString("redis.user");
        String password = c.getString("redis.password");

        this.parentFolder = c.getString("redis.parent-folder", "orbit");

        RedisURI uri = RedisURI.builder().withHost(host).withPort(port).withAuthentication(user, password).build();

        RedisClient client = RedisClient.create(uri);
        this.connection = client.connect();

//        StatefulRedisPubSubConnection<String, String> pubSubConnection = client.connectPubSub().async().getStatefulConnection();
//
//        pubSubConnection.addListener(new RedisPubSubAdapter<>() {
//            @Override
//            public void message(String channel, String message) {
//                if (!channel.equalsIgnoreCase("orbit_saved")) return;
//                UUID uuid = UUID.fromString(message);
//            }
//        });



    }

    public String getUserDataPath(String orbitIdentifier, UUID userUUID) {
        return this.parentFolder + ":user_data:" + orbitIdentifier + ":" + userUUID;
    }
//
//    public String getQuestDataPath(String questIdentifier, QuestAppearType appearType) {
//        return this.parentFolder + ":quest_data:" + appearType.name() + ":" + questIdentifier;
//    }

    public String encodeUnlockedTiersBitSetToString(BitSet bitSet, int size) {
        StringBuilder binaryStr = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            binaryStr.append(bitSet.get(i) ? "1" : "0");
        }
        return binaryStr.toString();
    }

    public @NotNull BitSet decodeUnlockedTiersStringToBitSet(@NotNull OrbitData orbitData, @Nullable String bitSetAsString) {
        BitSet bitSet = new BitSet(orbitData.tierAmount());

        if (bitSetAsString == null || bitSetAsString.isBlank()) return bitSet;

        for (int i = 0; i < orbitData.tierAmount(); i++) {
            if (bitSetAsString.charAt(i) == '1') bitSet.set(i);
        }

        return bitSet;
    }

    public RedisCommands<String, String> getSync() {
        return this.connection.sync();
    }

    public RedisAsyncCommands<String, String> getAsync() {
        return this.connection.async();
    }
}

package me.stivendarsi.orbit.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.UUID;

public class RedisHandler {
    private StatefulRedisConnection<String, String> connection;

    public void load() {
        RedisURI uri = RedisURI.builder().withHost("82.22.24.12").withPort(25595).withAuthentication("default", "A@pX#=sEk)x,luo5U5jx^G)Y&Vmi&oDGOr[&^Bly").build();

        RedisClient client = RedisClient.create(uri);
        this.connection = client.connect();
    }

    public static String getUserDataPath(String orbitIdentifier, UUID userUUID) {
        return "orbit_test:user_data:" + orbitIdentifier + ":" + userUUID;
    }

    public static String getQuestDataPath(String questIdentifier, QuestAppearType appearType) {
        return "orbit_test:quest_data:" + appearType.name() + ":" + questIdentifier;
    }

    public static String encodeUnlockedTiersBitSetToString(BitSet bitSet, int size) {
        StringBuilder binaryStr = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            binaryStr.append(bitSet.get(i) ? "1" : "0");
        }
        return binaryStr.toString();
    }

    public static @NotNull BitSet decodeUnlockedTiersStringToBitSet(@NotNull OrbitData orbitData, @Nullable String bitSetAsString) {
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

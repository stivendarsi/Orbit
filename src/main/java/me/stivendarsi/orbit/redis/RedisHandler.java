package me.stivendarsi.orbit.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class RedisHandler {
    private StatefulRedisConnection<String, String> connection;

    public void load() {
        RedisURI uri = RedisURI.builder().withHost("82.22.24.12").withPort(25595).withAuthentication("default", "A@pX#=sEk)x,luo5U5jx^G)Y&Vmi&oDGOr[&^Bly").build();

        RedisClient client = RedisClient.create(uri);
        this.connection = client.connect();
    }

    public static String getUserDataPath(String orbitIdentifier, UUID userUUID) {
        return "orbit:user_data:" + orbitIdentifier + ":" + userUUID;
    }

    public static String getQuestDataPath(String questIdentifier, QuestAppearType appearType) {
        return "orbit:quest_data:" + appearType.name() + ":" + questIdentifier;
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

    private String getOldUserDataKey(String orbitIdentifier, UUID userUUID, DataType dataType) {
        return "orbit:orbit_data:" + orbitIdentifier + ":" + userUUID + ":" + dataType.name();
    }


    public void migrateAllUsers() {
        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
        orbitInstance().getLogger().warning("Migrating all users...");
        long start = System.currentTimeMillis();
        migrateAllUsers(client);
        long end = System.currentTimeMillis();
        orbitInstance().getLogger().warning("Finished migrating all users" + " in " + (end - start) + " milliseconds.");
    }

    public void migrateUser(UUID userUUID) {
        RedisCommands<String, String> client = mainHandler().redisClient().getSync();

        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            migrateUserToNewDataBase(client, orbitIdentifier, userUUID);
        }

        orbitInstance().getLogger().warning("Done migrating user!");
    }

    private void migrateAllUsers(RedisCommands<String, String> client) {
        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            orbitInstance().getLogger().warning("Migrating all users under: " + orbitIdentifier);
            long start = System.currentTimeMillis();
            ScanCursor cursor = ScanCursor.INITIAL;
            List<String> keys = new ArrayList<>();

            do {
                KeyScanCursor<String> scan = client.scan(
                        cursor,
                        ScanArgs.Builder.matches("orbit:orbit_data:" + orbitIdentifier + ":*")
                );

                cursor = scan;
                keys.addAll(scan.getKeys());

            } while (!cursor.isFinished());

            for (String uuidString : keys) {
                UUID userUUID = UUID.fromString(uuidString);
                migrateUserToNewDataBase(client, orbitIdentifier, userUUID);
            }
            long end = System.currentTimeMillis();
            orbitInstance().getLogger().warning("Finished migrating all users under: " + orbitIdentifier + " in " + (end - start) + " milliseconds.");
        }
    }

    private void migrateUserToNewDataBase(RedisCommands<String, String> client, String orbitIdentifier, UUID userUUID) {
        orbitInstance().getLogger().warning("Migrating user: " + userUUID + ".");
        long start = System.currentTimeMillis();
        String experienceKey = getOldUserDataKey(orbitIdentifier, userUUID, DataType.experience);
        String regularKey = getOldUserDataKey(orbitIdentifier, userUUID, DataType.regular);
        String plusKey = getOldUserDataKey(orbitIdentifier, userUUID, DataType.plus);

        String experienceString = client.get(experienceKey);
        String regularString = client.get(regularKey);
        String plusString = client.get(plusKey);

        Map<String, String> data = new HashMap<>();
        data.put(DataType.experience.name(), experienceString);
        data.put(DataType.regular.name(), regularString);
        data.put(DataType.plus.name(), plusString);

        client.hset(getUserDataPath(orbitIdentifier, userUUID), data);

        long end = System.currentTimeMillis();
        orbitInstance().getLogger().warning("Finished migrating user: " + userUUID + " in " + (end - start) + " milliseconds.");
    }


    public RedisCommands<String, String> getSync() {
        return this.connection.sync();
    }

    public RedisAsyncCommands<String, String> getAsync() {
        return this.connection.async();
    }
}

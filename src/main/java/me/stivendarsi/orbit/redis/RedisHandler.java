package me.stivendarsi.orbit.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
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

        this.parentFolder = c.getString("parent-folder", "orbit");

        RedisURI uri = RedisURI.builder().withHost(host).withPort(port).withAuthentication(user, password).build();

        RedisClient client = RedisClient.create(uri);
        this.connection = client.connect();
    }

    public String getUserDataPath(String orbitIdentifier, UUID userUUID) {
        return this.parentFolder + ":user_data:" + orbitIdentifier + ":" + userUUID;
    }

    public String getQuestDataPath(String questIdentifier, QuestAppearType appearType) {
        return this.parentFolder + ":quest_data:" + appearType.name() + ":" + questIdentifier;
    }

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
//
//    private String getOldUserDataKey(String orbitIdentifier, UUID userUUID, DataType dataType) {
//        return "orbit:orbit_data:" + orbitIdentifier + ":" + userUUID + ":" + dataType.name();
//    }


//    public void migrateAllUsers() {
//        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
//        orbitInstance().getLogger().warning("Migrating all users...");
//        long start = System.currentTimeMillis();
//        migrateAllUsers(client);
//        long end = System.currentTimeMillis();
//        orbitInstance().getLogger().warning("Finished migrating all users" + " in " + (end - start) + " milliseconds.");
//    }
//
//    public void migrateUser(UUID userUUID) {
//        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
//
//        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
//            migrateUserToNewDataBase(client, orbitIdentifier, userUUID);
//        }
//
//        orbitInstance().getLogger().warning("Done migrating user!");
//    }
//
//    private void migrateAllUsers(RedisCommands<String, String> client) {
//        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
//            orbitInstance().getLogger().warning("Migrating all users under: " + orbitIdentifier);
//            long start = System.currentTimeMillis();
//            ScanCursor cursor = ScanCursor.INITIAL;
//            List<String> keys = new ArrayList<>();
//
//            do {
//                KeyScanCursor<String> scan = client.scan(
//                        cursor,
//                        ScanArgs.Builder.matches("orbit:orbit_data:" + orbitIdentifier + ":*")
//                );
//
//                cursor = scan;
//                keys.addAll(scan.getKeys());
//
//            } while (!cursor.isFinished());
//
//            for (String key : keys) {
//                String[] parts = key.split(":");
//
//                String uuidString = parts[3];
//
//                UUID userUUID = UUID.fromString(uuidString);
//                migrateUserToNewDataBase(client, orbitIdentifier, userUUID);
//            }
//
//            long end = System.currentTimeMillis();
//            orbitInstance().getLogger().warning("Finished migrating all users under: " + orbitIdentifier + " in " + (end - start) + " milliseconds.");
//        }
//    }

//    private void migrateUserToNewDataBase(RedisCommands<String, String> client, String orbitIdentifier, UUID userUUID) {
//        orbitInstance().getLogger().warning("Migrating user: " + userUUID + ", of orbit " + orbitIdentifier + ".");
//        long start = System.currentTimeMillis();
//        String experienceKey = getOldUserDataKey(orbitIdentifier, userUUID, DataType.experience);
//
//        System.out.println(experienceKey);
//
//        String regularKey = getOldUserDataKey(orbitIdentifier, userUUID, DataType.regular);
//        String plusKey = getOldUserDataKey(orbitIdentifier, userUUID, DataType.plus);
//
//
//        String experienceString = client.get(experienceKey);
//        String regularString = client.get(regularKey);
//        String plusString = client.get(plusKey);
//
//        orbitInstance().getLogger().warning("Old Experience: " + experienceString);
//
//        Map<String, String> data = new HashMap<>();
//        data.put(DataType.experience.name(), experienceString);
//        data.put(DataType.regular.name(), regularString);
//        data.put(DataType.plus.name(), plusString);
//
//        orbitInstance().getLogger().warning("New path: " + getUserDataPath(orbitIdentifier, userUUID));
//
//      //  client.hgetdel(getUserDataPath(orbitIdentifier, userUUID), data);
//
//        long end = System.currentTimeMillis();
//        orbitInstance().getLogger().warning("Finished migrating user: " + userUUID + " of orbit " + orbitIdentifier + ", in " + (end - start) + " milliseconds.");
//    }


    public RedisCommands<String, String> getSync() {
        return this.connection.sync();
    }

    public RedisAsyncCommands<String, String> getAsync() {
        return this.connection.async();
    }
}

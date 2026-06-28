package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.redis.DataType;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class UserHandler {
    private final Map<UUID, LocalUserData> userDataMap = new HashMap<>();

    public void load() {
        this.userDataMap.values().forEach(this::saveUser);
        this.userDataMap.clear();

        Bukkit.getOnlinePlayers().forEach(player -> {
            loadUser(player.getUniqueId());
        });
    }

    public void loadUser(UUID userUUID) {
        LocalUserData localUserData = new LocalUserData(userUUID);
        this.userDataMap.put(localUserData.userUUID(), localUserData);
    }

    public void unloadUser(UUID userUUID) {
        saveUser(userUUID);
        this.userDataMap.remove(userUUID);
    }

    private void saveUser(UUID userUUID) {
        saveUser(this.userDataMap.getOrDefault(userUUID, null));
    }

    private void saveUser(@Nullable LocalUserData localUserData) {
        if (localUserData == null) return;
        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            Pair<boolean[], boolean[]> t = localUserData.getTiersData(orbitIdentifier);
            Preconditions.checkNotNull(t, "Null tier data: " + orbitIdentifier);

            String experience = String.valueOf(localUserData.getUserExperience(orbitIdentifier));
            client.set(RedisHandler.getUserDataPath(orbitIdentifier, localUserData.userUUID(), DataType.experience), experience);

            boolean[] regular = t.getLeft();
            for (int i = 0; i < regular.length; i++) {
                int value = regular[i] ? 1 : 0;
                client.setbit(RedisHandler.getUserDataPath(orbitIdentifier, localUserData.userUUID(), DataType.regular), i, value);
            }

            boolean[] plus = t.getRight();
            for (int i = 0; i < plus.length; i++) {
                int value = plus[i] ? 1 : 0;
                client.setbit(RedisHandler.getUserDataPath(orbitIdentifier, localUserData.userUUID(), DataType.plus), i, value);
            }
        }
    }

    public @Nullable LocalUserData getUser(UUID uuid) {
        return this.userDataMap.getOrDefault(uuid, null);
    }
}

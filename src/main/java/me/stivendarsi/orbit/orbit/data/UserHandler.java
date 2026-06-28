package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.RedisClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class UserHandler {
    private final Map<UUID, UserData> userDataMap = new HashMap<>();

    public void load() {
        this.userDataMap.values().forEach(this::saveUser);
        this.userDataMap.clear();

        Bukkit.getOnlinePlayers().forEach(player -> {
            UserData userData = new UserData(player.getUniqueId());
            this.userDataMap.put(userData.userUUID(), userData);
        });
    }

    public void saveUser(UserData userData) {
        RedisClient client = mainHandler().redisClient().getClient();
        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            Pair<boolean[], boolean[]> t = userData.getTiersData(orbitIdentifier);
            Preconditions.checkNotNull(t, "Null tier data: " + orbitIdentifier);

            boolean[] regular = t.getLeft();
            for (int i = 0; i < regular.length; i++) {
                client.setbit(mainHandler().redisClient().getUserDataPath(orbitIdentifier, userData.userUUID(), RedisHandler.DataType.regular), i, regular[i]);
            }

            boolean[] plus = t.getRight();
            for (int i = 0; i < plus.length; i++) {
                client.setbit(mainHandler().redisClient().getUserDataPath(orbitIdentifier, userData.userUUID(), RedisHandler.DataType.plus), i, plus[i]);
            }
        }
    }

    public @Nullable UserData getUser(UUID uuid){
        return this.userDataMap.getOrDefault(uuid, null);
    }
}

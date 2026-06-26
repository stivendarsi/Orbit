package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.RedisClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class UserData {
    private int userExperience;
    private UUID userUUID;
    private Map<String, Pair<boolean[], boolean[]>> pairUnlocked;


    public UserData(UUID userUUID) {
        this.pairUnlocked = new HashMap<>();
        this.userUUID = userUUID;

        String userExperience = mainHandler().redisClient().getClient().hget("orbit:data:uuid", "experience");

        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            boolean[] regular = loadUnlockList(false, orbitIdentifier);
            boolean[] plus = loadUnlockList(true, orbitIdentifier);
            this.pairUnlocked.put(orbitIdentifier, Pair.of(regular, plus));
        }
    }

    public boolean[] loadUnlockList(boolean plus, String orbitIdentifier) {
        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        Preconditions.checkNotNull(orbitData, "Null orbit data");
        boolean[] unlocked = new boolean[orbitData.tierAmount()];
        RedisClient client = mainHandler().redisClient().getClient();
        String key;
        if (plus) key = "orbit:orbit_data:%s:%s:plus".formatted(orbitIdentifier, this.userUUID);
        else key = "orbit:orbit_data:%s:%s:regular".formatted(orbitIdentifier, this.userUUID);
        
        for (int i = 0; i < unlocked.length; i++) {
            unlocked[i] = client.getbit(key, i);
        }

        return unlocked;
    }
}

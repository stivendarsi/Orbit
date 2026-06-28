package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.RedisClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class UserData {
    private final Map<String, Integer> userOrbitExperience;
    private final UUID userUUID;
    private final Map<String, Pair<boolean[], boolean[]>> pairUnlocked;


    public UserData(UUID userUUID) {
        this.pairUnlocked = new HashMap<>();
        this.userOrbitExperience = new HashMap<>();
        this.userUUID = userUUID;


        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            boolean[] regular = loadUnlockList(false, orbitIdentifier);
            boolean[] plus = loadUnlockList(true, orbitIdentifier);
            this.pairUnlocked.put(orbitIdentifier, Pair.of(regular, plus));

            String userExperienceString = mainHandler().redisClient().getUserDataPath(orbitIdentifier, userUUID, RedisHandler.DataType.experience);
            this.userOrbitExperience.put(orbitIdentifier, NumberUtils.toInt(userExperienceString, 0));
        }
    }

    public void setUserOrbitExperience(String orbitIdentifier, int userOrbitExperience) {
        this.userOrbitExperience.put(orbitIdentifier, userOrbitExperience);
    }

    public int userExperience(String orbitIdentifier) {
        return userOrbitExperience.getOrDefault(orbitIdentifier, 0);
    }

    public @Nullable Pair<boolean[], boolean[]> getTiersData(String orbitIdentifier) {
        return this.pairUnlocked.getOrDefault(orbitIdentifier, null);
    }

    public void takePrize(String orbitIdentifier, int prizeIndex, boolean plus) {
        Pair<boolean[], boolean[]> data = this.pairUnlocked.getOrDefault(orbitIdentifier, null);
        Preconditions.checkNotNull(data, "Null tier data");

        if (plus) data.getRight()[prizeIndex] = true;
        else data.getLeft()[prizeIndex] = false;

        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
        Preconditions.checkNotNull(orbitData, "Null orbit data");
        Prize prize = orbitData.getPrize(prizeIndex, plus);

        if (prize == null) return;
        Player player = Bukkit.getPlayer(this.userUUID);

        Preconditions.checkNotNull(player, "Null player");
        prize.claimReward(player);

    }

    public boolean @NotNull [] loadUnlockList(boolean plus, String orbitIdentifier) {
        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);

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

    public UUID userUUID() {
        return userUUID;
    }
}

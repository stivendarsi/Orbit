package me.stivendarsi.orbit.orbit;

import com.google.common.base.Preconditions;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.redis.DataType;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

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

    public void saveAllData(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            unloadUser(onlinePlayer.getUniqueId());
        }
    }

    public void loadAllData(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            loadUser(onlinePlayer.getUniqueId());
        }
    }

    public void unloadUser(UUID userUUID) {
        saveUser(userUUID);
        this.userDataMap.remove(userUUID);
    }

    private void saveUser(UUID userUUID) {
        saveUser(this.userDataMap.getOrDefault(userUUID, null));
    }

    private void saveUser(@Nullable LocalUserData localUserData) {
        if (localUserData == null) {
            orbitInstance().getLogger().warning("Null user... returning");
            return;
        }
        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            Pair<BitSet, BitSet> t = localUserData.getTiersData(orbitIdentifier);
            Preconditions.checkNotNull(t, "Null tier data: " + orbitIdentifier);

            String experience = String.valueOf(localUserData.getUserExperience(orbitIdentifier));

            OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
            Preconditions.checkNotNull(orbitData, "Null orbit");


            String regularBitSet = RedisHandler.encodeUnlockedTiersBitSetToString(t.getLeft(), orbitData.tierAmount());
            String plusBitSet = RedisHandler.encodeUnlockedTiersBitSetToString(t.getRight(), orbitData.tierAmount());

            String userDataKey = RedisHandler.getUserDataPath(orbitIdentifier, localUserData.userUUID());

            Map<String, String> data = new HashMap<>();

            data.put(DataType.experience.name(), experience);
            data.put(DataType.plus.name(), plusBitSet);
            data.put(DataType.regular.name(), regularBitSet);

            client.hset(userDataKey, data);

        }
    }


    public @Nullable LocalUserData getUser(UUID uuid) {
        return this.userDataMap.getOrDefault(uuid, null);
    }
}

package me.stivendarsi.orbit.orbit;

import com.google.common.base.Preconditions;
import io.lettuce.core.HSetExArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisAclCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.redis.DataType;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZonedDateTime;
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

    public void saveAllData() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            unloadUser(onlinePlayer.getUniqueId());
        }
    }

    public void loadAllData() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            loadUser(onlinePlayer.getUniqueId());
        }
    }

    public void unloadUser(UUID userUUID) {
        LocalUserData localUserData = this.userDataMap.getOrDefault(userUUID, null);
        saveUser(localUserData);
        this.userDataMap.remove(userUUID);
    }

    private void saveUser(@Nullable LocalUserData localUserData) {
        if (localUserData == null) {
            orbitInstance().getLogger().warning("Null user... returning");
            return;
        }
        RedisAsyncCommands<String, String> client = mainHandler().redisClient().getAsync();

        OrbitData currentData = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentData == null) throw new RuntimeException("Null orbit");

        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            Pair<BitSet, BitSet> t = localUserData.getTiersData(orbitIdentifier);
            Preconditions.checkNotNull(t, "Null tier data: " + orbitIdentifier);

            String experience = String.valueOf(localUserData.getUserExperience(orbitIdentifier));

            OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
            Preconditions.checkNotNull(orbitData, "Null orbit");


            String regularBitSet = mainHandler().redisClient().encodeUnlockedTiersBitSetToString(t.getLeft(), orbitData.tierAmount());
            String plusBitSet = mainHandler().redisClient().encodeUnlockedTiersBitSetToString(t.getRight(), orbitData.tierAmount());

            String userDataKey = mainHandler().redisClient().getUserDataPath(orbitIdentifier, localUserData.userUUID());

            Map<String, String> data = new HashMap<>();

            data.put(DataType.experience.name(), experience);
            data.put(DataType.plus.name(), plusBitSet);
            data.put(DataType.regular.name(), regularBitSet);


            // Save seaon quests data
            for (QuestData value : orbitData.seasonQuests()) {
                data.put(value.questIdentifier(), String.valueOf(value.getUserCount(localUserData.userUUID())));
                value.removeUser(localUserData.userUUID());
            }

            client.hset(userDataKey, data);
        }

        Map<String, String> dailyQuestData = new HashMap<>();
        for (QuestData value : mainHandler().questHandler().dailyQuests()) {
            dailyQuestData.put(value.questIdentifier(), String.valueOf(value.getUserCount(localUserData.userUUID())));
            value.removeUser(localUserData.userUUID());
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        String key = mainHandler().redisClient().getUserDataPath(orbitData.identifier(), localUserData.userUUID());

        ZonedDateTime dailyQuestsTTL = mainHandler().questHandler().getNextDailyQuestTime();
        HSetExArgs exArgs = new HSetExArgs().exAt(dailyQuestsTTL.toInstant());

        mainHandler().redisClient().getAsync().hsetex(key, exArgs, dailyQuestData);
    }


    public @Nullable LocalUserData getUser(UUID uuid) {
        return this.userDataMap.getOrDefault(uuid, null);
    }
}

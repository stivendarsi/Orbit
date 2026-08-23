package me.stivendarsi.orbit.orbit;

import com.google.common.base.Preconditions;
import io.lettuce.core.HSetExArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.redis.DataType;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class UserHandler {
    private final Map<UUID, LocalUserData> registeredUsers = new HashMap<>();

    public void load() {
        this.registeredUsers.values().forEach(this::saveUserData);
        this.registeredUsers.clear();

        Bukkit.getOnlinePlayers().forEach(player -> {
            registerUser(player.getUniqueId());
        });
    }

    public void registerUser(UUID userUUID) {
        LocalUserData localUserData = new LocalUserData(userUUID);
        long start = System.currentTimeMillis();
        localUserData.loadUserDataAsync().thenAcceptAsync(_ -> {
            this.registeredUsers.put(localUserData.userUUID(), localUserData);
            if (mainHandler().messagesHandler().debugEnabled()) orbitInstance().getLogger().warning("Finished loading user: " + userUUID + " in " + (System.currentTimeMillis() - start) + " ms");
        });
    }

    public void saveAllData() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            unregisterUser(onlinePlayer.getUniqueId());
        }
    }

    public void unregisterUser(UUID userUUID) {
        LocalUserData localUserData = this.registeredUsers.getOrDefault(userUUID, null);
        if (localUserData != null) saveUserData(localUserData);
        this.registeredUsers.remove(userUUID);
    }

    private void saveUserData(@NotNull LocalUserData localUserData) {
        RedisAsyncCommands<String, String> client = mainHandler().redisClient().getAsync();

        OrbitData currentData = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentData == null) throw new RuntimeException("Null orbit");

        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            Pair<BitSet, BitSet> tiersData = localUserData.getTiersData(orbitIdentifier);
            Preconditions.checkNotNull(tiersData, "Null tiers data: " + orbitIdentifier); // continue; // Skip because of async use when loading tier data.

            OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
            Preconditions.checkNotNull(orbitData, "Null orbit");

            String experience = String.valueOf(localUserData.getUserExperience(orbitIdentifier));
            String regularBitSet = mainHandler().redisClient().encodeUnlockedTiersBitSetToString(tiersData.getLeft(), orbitData.tierAmount());
            String plusBitSet = mainHandler().redisClient().encodeUnlockedTiersBitSetToString(tiersData.getRight(), orbitData.tierAmount());

            String userDataKey = mainHandler().redisClient().getUserDataPath(orbitIdentifier, localUserData.userUUID());

            Map<String, String> data = new HashMap<>();

            data.put(DataType.experience.name(), experience);
            data.put(DataType.plus.name(), plusBitSet);
            data.put(DataType.regular.name(), regularBitSet);


            // Save current season quests data
            if (currentData.identifier().equalsIgnoreCase(orbitData.identifier())) {
                for (QuestData value : orbitData.seasonQuests()) {
                    data.put(value.questIdentifier(), String.valueOf(value.getUserProgress(localUserData.userUUID())));
                    value.removeUserProgress(localUserData.userUUID());
                }
            }

            client.hset(userDataKey, data);
        }

        Map<String, String> dailyQuestData = new HashMap<>();
        for (QuestData value : mainHandler().questHandler().dailyQuests()) {
            dailyQuestData.put(value.questIdentifier(), String.valueOf(value.getUserProgress(localUserData.userUUID())));
            value.removeUserProgress(localUserData.userUUID());
        }

        OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (orbitData == null) return;

        String key = mainHandler().redisClient().getUserDataPath(orbitData.identifier(), localUserData.userUUID());

        ZonedDateTime dailyQuestsTTL = mainHandler().questHandler().getNextDailyQuestTime();
        HSetExArgs exArgs = new HSetExArgs().exAt(dailyQuestsTTL.toInstant());

        mainHandler().redisClient().getAsync().hsetex(key, exArgs, dailyQuestData);
    }


    public @Nullable LocalUserData getUser(UUID uuid) {
        return this.registeredUsers.getOrDefault(uuid, null);
    }
}

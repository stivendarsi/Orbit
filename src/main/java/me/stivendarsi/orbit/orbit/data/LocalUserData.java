package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.redis.DataType;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class LocalUserData {
    private final Map<String, Integer> userOrbitExperience;
    private final UUID userUUID;
    private final Map<String, Pair<BitSet, BitSet>> pairUnlocked;


    public LocalUserData(UUID userUUID) {
        this.pairUnlocked = new HashMap<>();
        this.userOrbitExperience = new HashMap<>();
        this.userUUID = userUUID;


        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            BitSet regular = loadUnlockList(false, orbitIdentifier);
            BitSet plus = loadUnlockList(true, orbitIdentifier);
            this.pairUnlocked.put(orbitIdentifier, Pair.of(regular, plus));

            String userExperienceString = mainHandler().redisClient().getSync().get(RedisHandler.getUserDataPath(orbitIdentifier, userUUID, DataType.experience));
         //   System.out.println("User string experience: " + userExperienceString);
            this.userOrbitExperience.put(orbitIdentifier, NumberUtils.toInt(userExperienceString, 0));
        }
    }

    public void modifyUserExperience(String orbitIdentifier, int experience) {
        int current = this.userOrbitExperience.getOrDefault(orbitIdentifier, 0);
        setUserOrbitExperience(orbitIdentifier, current + experience);
    }

    public void setUserOrbitExperience(String orbitIdentifier, int userOrbitExperience) {
        this.userOrbitExperience.put(orbitIdentifier, userOrbitExperience);
    }

    public int getUserExperience(String orbitIdentifier) {
        return userOrbitExperience.getOrDefault(orbitIdentifier, 0);
    }

    public @Nullable Pair<BitSet, BitSet> getTiersData(String orbitIdentifier) {
        return this.pairUnlocked.getOrDefault(orbitIdentifier, null);
    }

    public void takePrize(String orbitIdentifier, int prizeIndex, boolean plus) {
        Pair<BitSet, BitSet> data = this.pairUnlocked.getOrDefault(orbitIdentifier, null);
        Preconditions.checkNotNull(data, "Null tier data");

        if (plus) data.getRight().set(prizeIndex, true);
        else data.getLeft().set(prizeIndex, true);

        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
        Preconditions.checkNotNull(orbitData, "Null orbit data");
        Prize prize = orbitData.getPrize(prizeIndex, plus);

        if (prize == null) return;
        Player player = Bukkit.getPlayer(this.userUUID);

        Preconditions.checkNotNull(player, "Null player");
        prize.claimReward(player);

    }

    public  @NotNull BitSet loadUnlockList(boolean plus, String orbitIdentifier) {
        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);

        Preconditions.checkNotNull(orbitData, "Null orbit data");

        String key;
        if (plus) key = RedisHandler.getUserDataPath(orbitIdentifier, this.userUUID, DataType.plus);
        else key = RedisHandler.getUserDataPath(orbitIdentifier, this.userUUID, DataType.regular);


        byte[] unlocked = mainHandler().redisClient().getSync().get(key).getBytes();
        if (unlocked.length != orbitData.tierAmount()) unlocked = Arrays.copyOf(unlocked, orbitData.tierAmount());

        return BitSet.valueOf(unlocked);
    }

    public UUID userUUID() {
        return userUUID;
    }
}

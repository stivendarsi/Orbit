package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import com.nexomc.nexo.glyphs.GlyphTag;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.redis.DataType;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.stivendarsi.orbit.Constants.runCommandInConsole;
import static me.stivendarsi.orbit.Orbit.mainHandler;

public class LocalUserData {
    private final Map<String, Integer> userOrbitExperience;
    private final Map<String, Pair<BitSet, BitSet>> pairUnlocked; // orbitId, regular | plus

    private final UUID userUUID;
    private Map<String, Set<UUID>> killedEntities; // questID, uuid of the entity killed.


    public LocalUserData(UUID userUUID) {
        this.pairUnlocked = new HashMap<>();
        this.userOrbitExperience = new HashMap<>();
        this.userUUID = userUUID;

        RedisCommands<String, String> client = mainHandler().redisClient().getSync();

        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {

            String key = RedisHandler.getUserDataPath(orbitIdentifier, userUUID);

            Map<String, String> data = client.hgetall(key);

            OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
            Preconditions.checkNotNull(orbitData, "Null orbit data");

            BitSet regular = RedisHandler.decodeUnlockedTiersStringToBitSet(orbitData, data.getOrDefault(DataType.regular.name(), null));
            BitSet plus =  RedisHandler.decodeUnlockedTiersStringToBitSet(orbitData, data.getOrDefault(DataType.plus.name(), null));

            this.pairUnlocked.put(orbitIdentifier, Pair.of(regular, plus));

            this.killedEntities = new HashMap<>();

            this.userOrbitExperience.put(orbitIdentifier, NumberUtils.toInt(data.getOrDefault(DataType.experience.name(), ""), 0));
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

    public void countKill(String questIdentifier, UUID killedUUID) {
        Set<UUID> killedEntities = getKilledEntities(questIdentifier);
        killedEntities.add(killedUUID);
        this.killedEntities.put(questIdentifier, killedEntities);
    }

    public Set<UUID> getKilledEntities(String questIdentifier) {
        Set<UUID> killedEntities = this.killedEntities.getOrDefault(questIdentifier, null);
        if (killedEntities == null) killedEntities = new HashSet<>();
        return killedEntities;
    }

    public Map<String, Set<UUID>> questEntityKilled() {
        return killedEntities;
    }

    public void takePrize(String orbitIdentifier, int prizeIndex, boolean plus) {
        Pair<BitSet, BitSet> data = this.pairUnlocked.getOrDefault(orbitIdentifier, null);
        Preconditions.checkNotNull(data, "Null tier data");

        //   System.out.println(prizeIndex + ": prize index");
        if (plus) data.getRight().set(prizeIndex, true);
        else data.getLeft().set(prizeIndex, true);

        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
        Preconditions.checkNotNull(orbitData, "Null orbit data");
        PrizeData prizeData = orbitData.getPrize(prizeIndex, plus);

        if (prizeData == null) {
            // System.out.println("Null prize data");
            return;
        }
        Player player = Bukkit.getPlayer(this.userUUID);

        Preconditions.checkNotNull(player, "Null player");
        boolean commandRan = runCommandInConsole(player, prizeData.getRewardCommand());
        if (!commandRan) return;

        player.playSound(Constants.pingSound);
        String msg = prizeData.rewardMessage();
        if (msg == null) return;

        player.sendRichMessage(msg, GlyphTag.INSTANCE.getRESOLVER(), MiniPlaceholders.audienceGlobalPlaceholders());
    }

//    public @NotNull BitSet phraseUnlockedTierBitSet(@NotNull OrbitData orbitData, @Nullable String bitSetAsString) {
//        BitSet bitSet = new BitSet(orbitData.tierAmount());
//
//        if (bitSetAsString == null || bitSetAsString.isBlank()) return bitSet;
//
//        for (int i = 0; i < orbitData.tierAmount(); i++) {
//            if (bitSetAsString.charAt(i) == '1') bitSet.set(i);
//        }
//
//        return bitSet;
//    }

//    public @NotNull BitSet loadUnlockList(boolean plus, String orbitIdentifier) {
//        OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
//
//        Preconditions.checkNotNull(orbitData, "Null orbit data");
//
//        String path;
//        if (plus) path = RedisHandler.getUserDataPath(orbitIdentifier, this.userUUID, DataType.plus);
//        else path = RedisHandler.getUserDataPath(orbitIdentifier, this.userUUID, DataType.regular);
//
//
//        BitSet bitSet = new BitSet(orbitData.tierAmount());
//
//        if (!RedisHandler.pathExists(path)) {
//            return bitSet;
//        }
//
//        String tierData = mainHandler().redisClient().getSync().get(path);
//        // System.out.println("Tier Data: " + tierData);
//
//        for (int i = 0; i < orbitData.tierAmount(); i++) {
//            if (tierData.charAt(i) == '1') bitSet.set(i);
//        }
//
//        return bitSet;
//    }

    public UUID userUUID() {
        return userUUID;
    }
}

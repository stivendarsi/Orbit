package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import com.nexomc.nexo.glyphs.GlyphTag;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.lettuce.core.api.async.RedisAsyncCommands;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.redis.DataType;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Constants.runCommandInConsole;
import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class LocalUserData {
    private final Map<String, Integer> userOrbitExperience; // OrbitId, experience
    private final Map<String, Pair<BitSet, BitSet>> pairUnlocked; // orbitId, regular | plus

    private final UUID userUUID;

    public LocalUserData(UUID userUUID) {
        this.pairUnlocked = new HashMap<>();
        this.userOrbitExperience = new HashMap<>();
        this.userUUID = userUUID;

        RedisAsyncCommands<String, String> client = mainHandler().redisClient().getAsync();

        for (String orbitIdentifier : mainHandler().orbitHandler().getOrbitIdentifiers()) {
            String key = mainHandler().redisClient().getUserDataPath(orbitIdentifier, userUUID);
            long startLoading = System.currentTimeMillis();
            client.hgetall(key).thenAccept(userData -> {
                orbitInstance().getLogger().warning(userData.toString());
                OrbitData orbitData = mainHandler().orbitHandler().getOrbit(orbitIdentifier);
                Preconditions.checkNotNull(orbitData, "Null orbit data");

                if (userData.isEmpty() && mainHandler().messagesHandler().debugEnabled())
                    orbitInstance().getLogger().warning("Creating new user data: " + userUUID);

                BitSet regular = mainHandler().redisClient().decodeUnlockedTiersStringToBitSet(orbitData, userData.getOrDefault(DataType.regular.name(), null));
                BitSet plus = mainHandler().redisClient().decodeUnlockedTiersStringToBitSet(orbitData, userData.getOrDefault(DataType.plus.name(), null));

                this.pairUnlocked.put(orbitIdentifier, Pair.of(regular, plus));
                this.userOrbitExperience.put(orbitIdentifier, NumberUtils.toInt(userData.getOrDefault(DataType.experience.name(), ""), 0));

                mainHandler().questHandler().loadUserDailyQuestData(userUUID, userData, orbitData);
                mainHandler().questHandler().loadUserSeasonQuestData(userUUID, userData, orbitData);
                if (mainHandler().messagesHandler().debugEnabled())
                    orbitInstance().getLogger().warning("Finished loading orbit data of " + orbitIdentifier + " for user in: " + (System.currentTimeMillis() - startLoading) + "ms");
            });
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
        PrizeData prizeData = orbitData.getPrize(prizeIndex, plus);

        if (prizeData == null) return;

        Player player = Bukkit.getPlayer(this.userUUID);

        Preconditions.checkNotNull(player, "Null player");
        boolean commandRan = runCommandInConsole(player, prizeData.getRewardCommand());
        if (!commandRan) return;

        player.playSound(Constants.pingSound);
        String msg = prizeData.rewardMessage();
        if (msg == null) return;

        player.sendRichMessage(msg, GlyphTag.INSTANCE.getRESOLVER(), MiniPlaceholders.audienceGlobalPlaceholders());
    }

    public UUID userUUID() {
        return userUUID;
    }
}

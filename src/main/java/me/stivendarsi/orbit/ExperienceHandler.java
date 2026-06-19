package me.stivendarsi.orbit;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.plugin;

public class ExperienceHandler {
    private Map<UUID, Integer> cache;

    public void load() {
        this.cache = new HashMap<>();

        // Load All Online Players
        for (Player onlinePlayer : plugin().getServer().getOnlinePlayers()) {
            loadUserFromRedis(onlinePlayer.getUniqueId());
        }
    }

    public void loadUserFromRedis(UUID userUUID) {
        int userExperience = getRedisExperience(userUUID);
        if (userExperience == -1) registerNewUser(userUUID);
        else this.cache.put(userUUID, userExperience);
    }


    public void registerNewUser(UUID userUUID) {
        this.cache.put(userUUID, 0);
        mainHandler().redisClient().getClient().set("orbit." + userUUID, String.valueOf(0));
    }

    public void modifyUserExperience(UUID userUUID, int experience) {
        int userExperience = getRedisExperience(userUUID);
        if (userExperience == -1) {
            plugin().getLogger().warning("Not modifying this user, because user " + userUUID + " does not exist in this Redis database.");
            return;
        }

        userExperience = Math.max(userExperience + experience, 0);

        if (this.cache.containsKey(userUUID)) this.cache.put(userUUID, userExperience);
        else mainHandler().redisClient().getClient().set("orbit." + userUUID, String.valueOf(userExperience));
    }


    public int getRedisExperience(UUID userUUID) {
        if (this.cache.containsKey(userUUID)) return this.cache.get(userUUID);

        String stringAmount = mainHandler().redisClient().getClient().get("orbit." + userUUID);
        if (stringAmount == null) return -1;
        else return Integer.parseInt(stringAmount);
    }
}

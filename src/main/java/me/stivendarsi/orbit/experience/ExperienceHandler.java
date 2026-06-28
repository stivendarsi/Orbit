package me.stivendarsi.orbit.experience;

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
        int userExperience = getExperience(userUUID);
        if (userExperience == -1) registerNewUser(userUUID); // Create new User if not exist.
        else this.cache.put(userUUID, userExperience);
    }


    public void registerNewUser(UUID userUUID) {
        this.cache.put(userUUID, 0);
        mainHandler().redisClient().getClient().set("orbit:" + userUUID, String.valueOf(0));
    }

    public void setUserExperience(UUID userUUID, int experience) {
        int userExperience = Math.max(experience, 0);

        if (this.cache.containsKey(userUUID)) this.cache.put(userUUID, userExperience);
        else mainHandler().redisClient().getClient().set("orbit:" + userUUID, String.valueOf(userExperience));
    }

    public void modifyUserExperience(UUID userUUID, int additionalExperience) {
        int currentUserExperience = getExperience(userUUID);
        if (currentUserExperience == -1) {
            plugin().getLogger().warning("Not modifying this user, because user " + userUUID + " does not exist in this Redis database.");
            return;
        }

        setUserExperience(userUUID, additionalExperience + currentUserExperience);
    }

    public void saveUserInRedis(UUID userUUID) {
        if (this.cache.containsKey(userUUID)) {
            mainHandler().redisClient().getClient().set("orbit:" + userUUID, String.valueOf(this.cache.get(userUUID)));
            System.out.println("saved user");
        } else System.out.println("not saved");
    }


    public int getExperience(UUID userUUID) {
        if (this.cache.containsKey(userUUID)) {
            System.out.println("Returning user amount");
            return this.cache.get(userUUID);
        }

        String stringAmount = mainHandler().redisClient().getClient().get("orbit:" + userUUID);
        if (stringAmount == null) return -1;
        else {
            int a = Integer.parseInt(stringAmount);
            System.out.println("amount: " + a);
            return a;
        }
    }
}

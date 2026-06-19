package me.stivendarsi.orbit;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class ExperienceHandler {
    private Map<UUID, Integer> cache = new HashMap<>();

    public void load(){
        
    }

    public void loadUser(UUID uuid){
        String s = mainHandler().redisClient().getClient().get("orbit." + uuid);
        int userExperience = 0;
        if (s == null) {
            updateUser(uuid, 0);
        }
        if (s != null) userExperience = Integer.parseInt(s);

        this.cache.put(uuid, userExperience);
    }


    public void registerNewUser(UUID userUUID){
        mainHandler().redisClient().getClient().set("orbit." + userUUID, String.valueOf(0));
    }

    public void registerNewUser(UUID userUUID){
        mainHandler().redisClient().getClient().set("orbit." + userUUID, String.valueOf(0));
    }

    public void updateUser(UUID uuid, int amount){
        int userExperience = this.cache.getOrDefault(uuid, 0);
        userExperience = Math.max(0, userExperience + amount);
        this.cache.put(uuid, userExperience);
    }

    public void saveCacheUser(UUID user){
        mainHandler().redisClient().getClient().setbit("orbit." + user, String.valueOf(this.cache.getOrDefault(user, 0)));
    }

    public void modifyUser(UUID user, int userExperience){
        mainHandler().redisClient().getClient().set("orbit." + user, String.valueOf(userExperience));
    }
}

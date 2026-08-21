package me.stivendarsi.orbit.orbit;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class OrbitHandler {
    private Map<String, OrbitData> orbits;
    private Map<String, Permission> orbitPermission;


    public void load() {
        unLoadPermissions();
        List<String> orbitIdentifiers = new ArrayList<>(orbitInstance().getConfig().getConfigurationSection("orbits").getKeys(false));
        this.orbits = new HashMap<>();

        while (!orbitIdentifiers.isEmpty()) {
            String orbitIdentifier = orbitIdentifiers.removeFirst();
            ConfigurationSection s = orbitInstance().getConfig().getConfigurationSection("orbits." + orbitIdentifier);
            if (s != null) this.orbits.put(orbitIdentifier, new OrbitData(orbitIdentifier, s));
        }
        loadPermissions();
    }

    public void unLoadPermissions(){
        if (this.orbitPermission == null || this.orbitPermission.isEmpty()) return;
        for (Permission permission : this.orbitPermission.values()) {
            orbitInstance().getServer().getPluginManager().removePermission(permission);
        }

        this.orbitPermission.clear();
    }

    public void loadPermissions() {
        this.orbitPermission = new HashMap<>();
        for (String orbitIdentifier : getOrbitIdentifiers()) {
            Permission orbitSeasonPermission = new Permission("orbit.access." + orbitIdentifier, PermissionDefault.FALSE);
            this.orbitPermission.put(orbitIdentifier, orbitSeasonPermission);
        }
    }

    public @Nullable Permission getOrbitPermission(String orbitIdentifier){
        return this.orbitPermission.getOrDefault(orbitIdentifier, null);
    }

    public @Nullable OrbitData getOrbit(String orbitIdentifier) {
        return this.orbits.values().stream().filter(orbitData -> orbitData.identifier().equals(orbitIdentifier)).findFirst().orElse(null);
    }

    public @Nullable OrbitData getCurrentOrbit() {
        ZonedDateTime now =  mainHandler().questHandler().getCurrentTime();
        return this.orbits.values().stream()
                .filter(orbit -> orbit.start().isBefore(now) && orbit.end().isAfter(now))
                .findFirst().orElse(null);
    }

    public String[] getOrbitIdentifiers() {
        return this.orbits.keySet().toArray(new String[0]);
    }
}

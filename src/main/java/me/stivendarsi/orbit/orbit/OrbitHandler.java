package me.stivendarsi.orbit.orbit;

import me.stivendarsi.orbit.orbit.data.OrbitData;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class OrbitHandler {
    private Map<String, OrbitData> orbits;

    public void load() {
        List<String> orbitIdentifiers = new ArrayList<>(orbitInstance().getConfig().getConfigurationSection("orbits").getKeys(false));
        this.orbits = new HashMap<>();

        while (!orbitIdentifiers.isEmpty()) {
            String orbitIdentifier = orbitIdentifiers.removeFirst();
            ConfigurationSection s = orbitInstance().getConfig().getConfigurationSection("orbits." + orbitIdentifier);
            if (s != null) this.orbits.put(orbitIdentifier, new OrbitData(orbitIdentifier, s));
        }
    }

    public @Nullable OrbitData getOrbit(String orbitIdentifier) {
        return this.orbits.values().stream().filter(orbitData -> orbitData.identifier().equals(orbitIdentifier)).findFirst().orElse(null);
    }

    public @Nullable OrbitData getCurrentOrbit() {
        return this.orbits.values().stream()
                .filter(orbit -> orbit.start().isBefore(LocalDateTime.now()) && orbit.end().isAfter(LocalDateTime.now()))
                .findFirst().orElse(null);
    }

    public String[] getOrbitIdentifiers() {
        return this.orbits.keySet().toArray(new String[0]);
    }
}

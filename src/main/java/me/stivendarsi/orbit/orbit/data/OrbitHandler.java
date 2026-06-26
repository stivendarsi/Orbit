package me.stivendarsi.orbit.orbit.data;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

import static me.stivendarsi.orbit.Orbit.plugin;

public class OrbitHandler {
    private OrbitData[] orbits;

    public void load() {
        List<String> orbitIdentifiers = new ArrayList<>(plugin().getConfig().getConfigurationSection("orbits").getKeys(false));
        this.orbits = new OrbitData[orbitIdentifiers.size()];

        int i = 0;
        while (!orbitIdentifiers.isEmpty()) {
            String orbitIdentifier = orbitIdentifiers.removeFirst();
            ConfigurationSection s = plugin().getConfig().getConfigurationSection("orbits." + orbitIdentifier);
            if (s == null) continue;
            this.orbits[i++] = new OrbitData(orbitIdentifier, s);
        }

        Arrays.sort(orbits, Comparator.comparing(OrbitData::start));
    }

    public @Nullable OrbitData getCurrentOrbit() {
        Optional<OrbitData> orbitData = Arrays.stream(orbits).filter(orbit -> orbit.end().isAfter(LocalDateTime.now()) && orbit.start().isBefore(LocalDateTime.now())).findFirst();
        return orbitData.orElse(null);
    }

    public String[] getOrbitIdentifiers() {
        return Arrays.stream(orbits)
                .map(OrbitData::identifier)
                .toArray(String[]::new);
    }
}

package me.stivendarsi.orbit.orbit.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
}

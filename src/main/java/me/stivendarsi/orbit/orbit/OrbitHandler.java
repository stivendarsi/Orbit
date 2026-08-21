package me.stivendarsi.orbit.orbit;

import io.netty.util.internal.StringUtil;
import me.stivendarsi.orbit.Orbit;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class OrbitHandler {
    private Map<String, OrbitData> orbits;
    private Map<String, Permission> orbitPermission;

    private OrbitData currentOrbitData;


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
        updateCurrentOrbit();
    }

    public void unLoadPermissions() {
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

    public @Nullable Permission getOrbitPermission(String orbitIdentifier) {
        return this.orbitPermission.getOrDefault(orbitIdentifier, null);
    }

    public @Nullable OrbitData getOrbit(String orbitIdentifier) {
        return this.orbits.values().stream().filter(orbitData -> orbitData.identifier().equals(orbitIdentifier)).findFirst().orElse(null);
    }

    public @Nullable OrbitData getCurrentOrbit() {
        return this.currentOrbitData;
    }

    public void updateCurrentOrbit() {
        String previousId = "No previous orbit id";
        if (this.currentOrbitData != null) previousId = this.currentOrbitData.identifier();
        ZonedDateTime now = mainHandler().questHandler().getCurrentTime();
        this.currentOrbitData = this.orbits.values().stream()
                .filter(orbit -> orbit.start().isBefore(now) && orbit.end().isAfter(now))
                .findFirst().orElse(null);
        String currentID = "No current orbit id";
        if (this.currentOrbitData != null) currentID = this.currentOrbitData.identifier();
        if (mainHandler().messagesHandler().debugEnabled()) orbitInstance().getLogger().warning("Previous orbit ID: " + previousId + ", current orbit ID: " + currentID);
    }

    public String[] getOrbitIdentifiers() {
        return this.orbits.keySet().toArray(new String[0]);
    }

    public void startHandleOrbitChange() {
        OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbit == null) return;

        long timeLeft = Duration.between(mainHandler().questHandler().getCurrentTime(), currentOrbit.end()).toSeconds();

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), _ -> {
            OrbitData current = mainHandler().orbitHandler().getCurrentOrbit();
            if (current == null) return;
            for (QuestData seasonQuest : current.seasonQuests()) {
                seasonQuest.resetCounter();
            }
            updateCurrentOrbit();
        }, timeLeft, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }
}
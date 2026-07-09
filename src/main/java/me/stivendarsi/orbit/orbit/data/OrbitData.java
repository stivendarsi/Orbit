package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrbitData {
    private final String identifier;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final int tierAmount;
    private final int levelMultiplier;
    private final Pair<Prize, Prize>[] tiers;

    public OrbitData(String orbitIdentifier, ConfigurationSection orbitSection) {
        this.identifier = orbitIdentifier;

        String startString = orbitSection.getString("start");
        String endString = orbitSection.getString("end");

        Preconditions.checkNotNull(startString, "Null start string: " + orbitIdentifier);
        Preconditions.checkNotNull(endString, "Null end string: " + orbitIdentifier);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss");
        this.start = LocalDateTime.parse(startString, formatter);
        this.end = LocalDateTime.parse(endString, formatter);

        this.tierAmount = orbitSection.getInt("tier-amount");
        this.levelMultiplier = orbitSection.getInt("level-multiplier");

        this.tiers = new Pair[tierAmount];

        for (int levelIndex = 0; levelIndex < this.tierAmount; levelIndex++) {
            this.tiers[levelIndex] = loadTier(levelIndex, orbitSection);
        }
    }

    private Pair<Prize, Prize> loadTier(int levelIndex, ConfigurationSection orbitSection) {
        ConfigurationSection regularSection = orbitSection.getConfigurationSection("tiers.%s.regular".formatted(levelIndex));
        Prize regular = null;
        if (regularSection != null) regular = new Prize(levelIndex, false, regularSection);

        ConfigurationSection plusSection = orbitSection.getConfigurationSection("tiers.%s.plus".formatted(levelIndex));
        Prize plus = null;
        if (plusSection != null) plus = new Prize(levelIndex, true, plusSection);

        return Pair.of(regular, plus);
    }


    public @Nullable Prize getPrize(int levelIndex, boolean plus) {
        Pair<Prize, Prize> tier = this.tiers[levelIndex];
        if (tier == null) return null;
        return plus ? tier.getRight() : tier.getLeft();
    }

    public Pair<Prize, Prize>[] tiers() {
        return tiers;
    }

    public String identifier() {
        return identifier;
    }

    public int tierAmount() {
        return tierAmount;
    }

    public int levelMultiplier() {
        return levelMultiplier;
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return end;
    }
}

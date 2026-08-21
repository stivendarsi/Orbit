package me.stivendarsi.orbit.orbit.data;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.quest.QuestData;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.quest.QuestHandler.ORBIT_DATE_TIME_FORMATTER;

public class OrbitData {
    private final String identifier;
    private final String title;
    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final int tierAmount;
    private final int levelMultiplier;
    private final Pair<PrizeData, PrizeData>[] tiers;
    private final List<QuestData> seasonQuests;

    public OrbitData(String orbitIdentifier, ConfigurationSection orbitSection) {
        this.identifier = orbitIdentifier;

        this.title = orbitSection.getString("title");

        String startString = orbitSection.getString("start");
        String endString = orbitSection.getString("end");

        Preconditions.checkNotNull(startString, "Null start string: " + orbitIdentifier);
        Preconditions.checkNotNull(endString, "Null end string: " + orbitIdentifier);


        this.start = ZonedDateTime.parse(startString, ORBIT_DATE_TIME_FORMATTER);
        this.end = ZonedDateTime.parse(endString, ORBIT_DATE_TIME_FORMATTER);

        this.tierAmount = orbitSection.getInt("tier-amount");
        this.levelMultiplier = orbitSection.getInt("level-multiplier");

        List<String> seasonQuestsIdentifiers = orbitSection.getStringList("season-quests");

        this.seasonQuests = seasonQuestsIdentifiers.stream().map(s -> mainHandler().questHandler().getQuestData(s)).filter(Objects::nonNull).toList();

        this.tiers = new Pair[tierAmount];

        for (int levelIndex = 0; levelIndex < this.tierAmount; levelIndex++) {
            this.tiers[levelIndex] = loadTier(levelIndex, orbitSection);
        }
    }

    private Pair<PrizeData, PrizeData> loadTier(int levelIndex, ConfigurationSection orbitSection) {
        ConfigurationSection regularSection = orbitSection.getConfigurationSection("tiers.%s.regular".formatted(levelIndex));
        PrizeData regular = null;
        if (regularSection != null) regular = new PrizeData(levelIndex, false, regularSection);

        ConfigurationSection plusSection = orbitSection.getConfigurationSection("tiers.%s.plus".formatted(levelIndex));
        PrizeData plus = null;
        if (plusSection != null) plus = new PrizeData(levelIndex, true, plusSection);

        return Pair.of(regular, plus);
    }

    public @Nullable PrizeData getPrize(int levelIndex, boolean plus) {
        Pair<PrizeData, PrizeData> tier = this.tiers[levelIndex];
        if (tier == null) return null;
        return plus ? tier.getRight() : tier.getLeft();
    }

    public Pair<PrizeData, PrizeData>[] tiers() {
        return tiers;
    }

    public String identifier() {
        return identifier;
    }

    public List<QuestData> seasonQuests() {
        return seasonQuests;
    }

    public int tierAmount() {
        return tierAmount;
    }

    public int levelMultiplier() {
        return levelMultiplier;
    }

    public String title() {
        return title;
    }

    public ZonedDateTime start() {
        return start;
    }

    public ZonedDateTime end() {
        return end;
    }
}

package me.stivendarsi.orbit.experience;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.stivendarsi.orbit.Orbit.plugin;

public class QuestHandler {
    private Map<String, Quest> questMap;

    private List<Quest> dailyQuests;

    public void load() {
        this.questMap = new HashMap<>();
        ConfigurationSection questsSection = plugin().getConfig().getConfigurationSection("quests");
        if (questsSection == null) return;


        for (String questIdentifier : questsSection.getKeys(false)) {
            Quest quest = new Quest(questIdentifier, questsSection.getConfigurationSection(questIdentifier));
            this.questMap.put(questIdentifier, quest);
        }

        this.dailyQuests = getQuestsOfTheDay(2); // load today's quests

    }

    public @Nullable Quest getQuest(String questIdentifier) {
        return this.questMap.getOrDefault(questIdentifier, null);
    }

    public Collection<Quest> getQuests() {
        return this.questMap.values();
    }

    public List<Quest> dailyQuests() {
        return dailyQuests;
    }

    public void startDailyQuestChanging() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .plusDays(1);


        long secondsLeftToMidnight = Duration.between(now, midnight).getSeconds();

        plugin().getServer().getAsyncScheduler().runAtFixedRate(plugin(), task -> {
            this.dailyQuests = getQuestsOfTheDay(2);
        }, secondsLeftToMidnight, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public List<Quest> getQuestsOfTheDay(int numberOfQuests) {
        Preconditions.checkState(numberOfQuests <= this.questMap.size());
        LocalDate today = LocalDate.now();
        long seed = today.getYear() * 10000L + today.getMonthValue() * 100L + today.getDayOfMonth();

        List<Quest> copy = new ArrayList<>(this.questMap.values()
                .stream()
                .filter(q -> q.appearType() == Quest.APPEAR_TYPE.DAILY)
                .toList());

        Collections.shuffle(copy, new Random(seed));
        return copy.subList(0, numberOfQuests);
    }
}

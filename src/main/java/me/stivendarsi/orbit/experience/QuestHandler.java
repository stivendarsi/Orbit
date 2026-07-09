package me.stivendarsi.orbit.experience;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.experience.enums.QuestAppearType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class QuestHandler {
    private Map<String, Quest> questMap;

    private List<Quest> dailyQuests;

    public void load() {
        this.questMap = new HashMap<>();
        ConfigurationSection questsSection = orbitInstance().getConfig().getConfigurationSection("quests");
        if (questsSection == null) return;

        for (String questIdentifier : questsSection.getKeys(false)) {
            Quest quest = new Quest(questIdentifier, questsSection.getConfigurationSection(questIdentifier));
            this.questMap.put(questIdentifier, quest);
        }

        this.dailyQuests = getQuestsOfTheDay(2); // load today's quests
    }

    public void loadUserQuestData(UUID uuid) {
        for (Quest value : this.questMap.values()) {
            value.loadUserQuestData(uuid);
        }
    }

    public @Nullable Quest getQuest(String questIdentifier){
        return this.questMap.getOrDefault(questIdentifier, null);
    }

    public void saveUserQuestData(UUID uuid){
        for (Quest value : this.questMap.values()) {
            value.saveUserQuestData(uuid);
        }
    }

    public List<Quest> dailyQuests() {
        return dailyQuests;
    }

    public void startDailyQuestChanging() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jerusalem"));
        LocalDateTime midnight = now
                .withHour(15)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .plusDays(1);


        long secondsLeftToMidnight = Duration.between(now, midnight).getSeconds();

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), task -> {
            resetDailyQuestData();
            this.dailyQuests = getQuestsOfTheDay(2);
        }, secondsLeftToMidnight, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void resetDailyQuestData(){
        mainHandler().redisClient().getSync().del("orbit:quest_data:daily");
    }

    public List<Quest> getQuestsOfTheDay(int numberOfQuests) {
        Preconditions.checkState(numberOfQuests <= this.questMap.size());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Jerusalem"));
        long seed = today.getYear() * 10000L + today.getMonthValue() * 100L + today.getDayOfMonth(); // 20260101

        List<Quest> copy = new ArrayList<>(this.questMap.values()
                .stream()
                .filter(q -> q.appearType() == QuestAppearType.DAILY)
                .toList());

        Collections.shuffle(copy, new Random(seed));
        return copy.subList(0, numberOfQuests);
    }
}

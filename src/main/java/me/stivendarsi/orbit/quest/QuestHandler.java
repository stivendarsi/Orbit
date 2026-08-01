package me.stivendarsi.orbit.quest;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import me.stivendarsi.orbit.quest.enums.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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
    private Map<String, QuestData> questMap;

    private List<QuestData> dailyQuestData;

    public void load() {
        this.questMap = new HashMap<>();
        ConfigurationSection questsSection = orbitInstance().getConfig().getConfigurationSection("quests");
        if (questsSection == null) return;

        for (String questIdentifier : questsSection.getKeys(false)) {
            QuestData questData = new QuestData(questIdentifier, questsSection.getConfigurationSection(questIdentifier));
            this.questMap.put(questIdentifier, questData);
        }

        this.dailyQuestData = getQuestsOfTheDay(2); // load today's quests
    }

    public void saveAllData(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            saveUserQuestData(onlinePlayer.getUniqueId());
        }
    }

    public void loadAllData(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            loadUserQuestData(onlinePlayer.getUniqueId());
        }
    }

    public void loadUserQuestData(UUID uuid) {
        for (QuestData value : this.questMap.values()) {
            value.loadUserQuestData(uuid);
        }
    }

    public @Nullable QuestData getQuestData(String questIdentifier) {
        return this.questMap.getOrDefault(questIdentifier, null);
    }

    public void saveUserQuestData(UUID uuid) {
        for (QuestData value : this.questMap.values()) {
            value.saveUserQuestData(uuid);
        }
    }


    public void startDailyQuestChanging() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jerusalem"));

        LocalDateTime nextRun = now.withHour(15)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        if (!now.isBefore(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        long secondsLeftToMidnight = calculateSecondsLeft(nextRun);

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), task -> {
            resetDailyQuestData();
            this.dailyQuestData = getQuestsOfTheDay(2);
        }, secondsLeftToMidnight, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void startSeasonQuestChanging() {
        OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbit == null) return;

        long secondsLeftToEndOfSeason = calculateSecondsLeft(currentOrbit.end());

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), task -> {
            resetSeasonQuestData();
        }, secondsLeftToEndOfSeason, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }


    private long calculateSecondsLeft(LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jerusalem"));
        return Duration.between(now, end).toSeconds();
    }


    private void resetDailyQuestData() {
        mainHandler().redisClient().getSync().del("orbit:quest_data:daily");
    }

    private void resetSeasonQuestData() {
        mainHandler().redisClient().getSync().del("orbit:quest_data:season");
    }


    public List<QuestData> getQuestsOfTheDay(int numberOfQuests) {
        return getAppearTypeBasedQuests(LocalDate.now(ZoneId.of("Asia/Jerusalem")), QuestAppearType.DAILY, numberOfQuests);
    }

    private List<QuestData> getAppearTypeBasedQuests(LocalDate localDate, QuestAppearType appearType, int numberOfQuests) {
        Preconditions.checkState(numberOfQuests <= this.questMap.size());
        long seed = getSeedBasedOnDate(localDate);
        List<QuestData> copy = new ArrayList<>(this.questMap.values()
                .stream()
                .filter(q -> q.appearType() == appearType)
                .toList());

        Collections.shuffle(copy, new Random(seed));
        return copy.subList(0, numberOfQuests);
    }

    private long getSeedBasedOnDate(LocalDate date) {
        return date.getYear() * 10000L + date.getMonthValue() * 100L + date.getDayOfMonth();
    }

    public List<QuestData> dailyQuests() {
        return dailyQuestData;
    }
}

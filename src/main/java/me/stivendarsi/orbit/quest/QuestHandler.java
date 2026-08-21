package me.stivendarsi.orbit.quest;

import com.google.common.base.Preconditions;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class QuestHandler {
    private Map<String, QuestData> questMap;
    private final int DAILY_QUESTS_RESET_HOUR = 15;
    public static final ZoneId ISRAEL_ZONE_ID = ZoneId.of("Asia/Jerusalem");
    public static final DateTimeFormatter ORBIT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss").withZone(ISRAEL_ZONE_ID);

    private List<QuestData> dailyQuestData;

    public void load() {
        this.questMap = new HashMap<>();
        ConfigurationSection questsSection = orbitInstance().getConfig().getConfigurationSection("quests");
        if (questsSection == null) return;

        for (String questIdentifier : questsSection.getKeys(false)) {

            ConfigurationSection orbitConfigurationSection = questsSection.getConfigurationSection(questIdentifier);
            if (orbitConfigurationSection == null) continue;

            QuestData questData = new QuestData(questIdentifier, orbitConfigurationSection);
            this.questMap.put(questIdentifier, questData);
        }

        this.dailyQuestData = getQuestsOfTheDay(2); // load today's quests
    }

    public void loadUserDailyQuestData(UUID user, Map<String, String> userData, OrbitData orbitData) {
        OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbit == null || !currentOrbit.identifier().equalsIgnoreCase(orbitData.identifier())) return;
        for (QuestData dailyQuestData : dailyQuestData) {
            int amount = NumberUtils.toInt(userData.getOrDefault(dailyQuestData.questIdentifier(), null), 0);
            dailyQuestData.countUser(user, amount);
        }
    }

    public void loadUserSeasonQuestData(UUID user, Map<String, String> userData, OrbitData orbitData) {
        OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbit == null|| !currentOrbit.identifier().equalsIgnoreCase(orbitData.identifier())) return;
        for (QuestData seasonQuestData : currentOrbit.seasonQuests()) {
            int amount = NumberUtils.toInt(
                    userData.getOrDefault(seasonQuestData.questIdentifier(), null),
                    0
            );
            seasonQuestData.countUser(user, amount);
        }
    }

    public @Nullable QuestData getQuestData(String questIdentifier) {
        return this.questMap.getOrDefault(questIdentifier, null);
    }


    public void startDailyQuestChanging() {
        long seconds = dailyQuestTimeLeft().toSeconds();
        orbitInstance().getLogger().warning("Resting Daily Quests in: " + timeLeftAsString());

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), task -> {
            for (QuestData dailyQuest : this.dailyQuests()) {
                dailyQuest.resetCounter();
            }
            this.dailyQuestData = getQuestsOfTheDay(2);
            orbitInstance().getLogger().warning("Restarted Daily Quests");
            orbitInstance().getLogger().warning("Next Daily Quests Reset in: " + timeLeftAsString());
        }, seconds, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }


    public String timeLeftAsString() {
        return DurationFormatUtils.formatDuration(dailyQuestTimeLeft().toMillis(), "yyyy-MM-dd HH:mm:ss");
    }

    public Duration dailyQuestTimeLeft() {
        ZonedDateTime nextQuestTime = getNextDailyQuestTime();

        ZonedDateTime now = getCurrentTime();

        return Duration.between(now, nextQuestTime);
    }

    public ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now(ISRAEL_ZONE_ID);
    }

    public ZonedDateTime getNextDailyQuestTime() {
        ZonedDateTime now = getCurrentTime();
        ZonedDateTime next = now.withHour(DAILY_QUESTS_RESET_HOUR).withMinute(0).withSecond(3).withNano(0);
        if (next.isBefore(now)) next = next.plusDays(1);
        return next;
    }

//    private long calculateSecondsLeft(LocalDateTime end) {
//        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jerusalem"));
//        return Duration.between(now, end).toSeconds();
//    }
//
//    private void resetQuestData(String key, QuestAppearType appearType) {
//        if (Objects.requireNonNull(appearType) == QuestAppearType.daily) {
//
//            for (QuestData questData : mainHandler().questHandler().dailyQuests()) {
//                questData.resetCounter(); // Reset the counter.
//            }
//
//            mainHandler().questHandler().dailyQuests().clear();
//        }
//
//
//        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
//        ScanCursor cursor = ScanCursor.INITIAL;
//        do {
//            KeyScanCursor<String> scan = client.scan(
//                    cursor,
//                    ScanArgs.Builder.matches("orbit:quest_data:daily:*")
//            );
//
//            cursor = scan;
//
//            if (!scan.getKeys().isEmpty()) {
//                client.unlink(scan.getKeys().toArray(new String[0]));
//            }
//        } while (!cursor.isFinished());
//    }


    public List<QuestData> getQuestsOfTheDay(int numberOfQuests) {
        return getAppearTypeBasedQuests(getCurrentTime(), QuestAppearType.daily, numberOfQuests);
    }

    private List<QuestData> getAppearTypeBasedQuests(ZonedDateTime now, QuestAppearType appearType, int numberOfQuests) {
        Preconditions.checkState(numberOfQuests <= this.questMap.size());
        long seed = getSeedBasedOnDate(now);
        List<QuestData> copy = new ArrayList<>(
                this.questMap.values().stream()
                        .filter(q -> q.appearType() == appearType)
                        .toList()
        );

        Preconditions.checkState(numberOfQuests <= copy.size());

        orbitInstance().getLogger().warning("Quest Seed: " + seed);

        Collections.shuffle(copy, new Random(seed));
        return copy.subList(0, numberOfQuests);
    }

    private long getSeedBasedOnDate(ZonedDateTime date) {
        if (DAILY_QUESTS_RESET_HOUR <= date.getHour())
            date = date.plusDays(1); // Offset the day to handle same-date-defferent-quests. if the time is after 15:00
        return date.getYear() * 10000L + date.getMonthValue() * 100L + date.getDayOfMonth();
    }

    public List<QuestData> dailyQuests() {
        return dailyQuestData;
    }
}

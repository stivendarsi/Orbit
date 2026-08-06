package me.stivendarsi.orbit.quest;

import com.google.common.base.Preconditions;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.enums.QuestAppearType;
import me.stivendarsi.orbit.redis.RedisHandler;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class QuestHandler {
    private Map<String, QuestData> questMap;
    private final int DAILY_QUESTS_RESET_HOUR = 15;

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

    public void saveAllData() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            saveUserQuestData(onlinePlayer.getUniqueId());
        }
    }

    public void loadAllData() {
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
        long seconds = timeLeft().toSeconds();
        orbitInstance().getLogger().warning("Resting Daily Quests in: " + timeLeftAsString());

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), task -> {
            String key = RedisHandler.getQuestDataPath("*", QuestAppearType.daily);

            resetQuestData(key, QuestAppearType.daily);
            this.dailyQuestData = getQuestsOfTheDay(2);
            orbitInstance().getLogger().warning("Restarted Daily Quests");
            orbitInstance().getLogger().warning("Next Daily Quests Reset in: " + timeLeftAsString());
        }, seconds, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void startSeasonQuestChanging() {
        OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbit == null) return;

        long secondsLeftToEndOfSeason = calculateSecondsLeft(currentOrbit.end());

        orbitInstance().getServer().getAsyncScheduler().runAtFixedRate(orbitInstance(), task -> {
            String key = RedisHandler.getQuestDataPath("*", QuestAppearType.season);
            resetQuestData(key, QuestAppearType.season);
        }, secondsLeftToEndOfSeason, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }


    public String timeLeftAsString() {
        return DurationFormatUtils.formatDuration(timeLeft().toMillis(), "yyyy-MM-dd HH:mm:ss");
    }

    private Duration timeLeft() {
        ZonedDateTime nextQuestTime = getNextQuestTime();

        ZonedDateTime now = getCurrentTime();

        return Duration.between(now, nextQuestTime);
    }

    public ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Jerusalem"));
    }

    public ZonedDateTime getNextQuestTime() {
        ZonedDateTime now = getCurrentTime();
        ZonedDateTime next = now.withHour(DAILY_QUESTS_RESET_HOUR).withMinute(0).withSecond(3).withNano(0);
        if (next.isBefore(now)) next = next.plusDays(1);
        return next;
    }


    private long calculateSecondsLeft(LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jerusalem"));
        return Duration.between(now, end).toSeconds();
    }

    private void resetQuestData(String key, QuestAppearType appearType) {
        if (Objects.requireNonNull(appearType) == QuestAppearType.daily) {
            mainHandler().questHandler().dailyQuests().clear();
        }


        RedisCommands<String, String> client = mainHandler().redisClient().getSync();
        ScanCursor cursor = ScanCursor.INITIAL;
        do {
            KeyScanCursor<String> scan = client.scan(
                    cursor,
                    ScanArgs.Builder.matches("orbit:quest_data:daily:*")
            );

            cursor = scan;

            if (!scan.getKeys().isEmpty()) {
                client.unlink(scan.getKeys().toArray(new String[0]));
            }
        } while (!cursor.isFinished());
    }


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

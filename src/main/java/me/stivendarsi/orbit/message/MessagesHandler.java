package me.stivendarsi.orbit.message;

import org.bukkit.configuration.file.FileConfiguration;

import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class MessagesHandler {
    private String regularName;
    private String orbitRegularDescription;
    private String orbitPlusName;
    private String orbitPlusDescription;
    private String canRedeem;
    private String cannotRedeem;
    private String redeemed;
    private String noOrbitPermission;
    private String orbitTitlePrefix;

    private String tierLocked;
    private String tierUnlocked;
    private String levelDescription;
    private String tierLevelLocked;
    private String tierLevelUnlocked;
    private String levelTitle;

    private String questsInfo;
    private String questsDailyReset;
    private String questsSeasonReset;

    private String orbitInfo;
    private String nextPage;
    private String previousPage;
    private String backButton;

    private boolean debug;

    public void load() {
        FileConfiguration config = orbitInstance().getConfig();
        debug = config.getBoolean("debug");
       // existDialog = config.getString("messages.exist-dialog", "");

        // Orbit
        orbitTitlePrefix = config.getString("messages.orbit.title-prefix", "");
        orbitPlusName = config.getString("messages.orbit.plus-name", "");
        orbitPlusDescription = config.getString("messages.orbit.plus-description", "");
        regularName = config.getString("messages.orbit.regular-name", "");
        orbitRegularDescription = config.getString("messages.orbit.regular-description", "");

        noOrbitPermission = config.getString("messages.orbit.no-permission", "");
        redeemed = config.getString("messages.orbit.redeemed", "");
        canRedeem = config.getString("messages.orbit.can-redeem", "");
        cannotRedeem = config.getString("messages.orbit.cannot-redeem", "");

        nextPage = config.getString("messages.orbit.next-page", "");
        previousPage = config.getString("messages.orbit.previous-page", "");
        backButton = config.getString("messages.orbit.back-button", "");

        orbitInfo = String.join("<newline>", config.getStringList("messages.orbit.info"));

        questsInfo = String.join("<newline>", config.getStringList("messages.quests.info"));
        questsDailyReset = String.join("<newline>", config.getStringList("messages.quests.daily-reset"));
        questsSeasonReset = String.join("<newline>", config.getStringList("messages.quests.season-reset"));

        tierLocked = config.getString("messages.tiers.locked", "");
        tierUnlocked = config.getString("messages.tiers.unlocked", "");
        levelTitle = config.getString("messages.tiers.level-title", "");
        levelDescription = config.getString("messages.tiers.level-description", "");
        tierLevelLocked = config.getString("messages.tiers.level-locked", "");
        tierLevelUnlocked = config.getString("messages.tiers.level-unlocked", "");
    }

    public String getLevelTitle() {
        return levelTitle;
    }


    public String getNextPage() {
        return nextPage;
    }

    public String getBackButton() {
        return backButton;
    }

    public String getPreviousPage() {
        return previousPage;
    }

    public String getRegularName() {
        return regularName;
    }

    public String getOrbitRegularDescription() {
        return orbitRegularDescription;
    }

    public String getOrbitPlusName() {
        return orbitPlusName;
    }

    public String getNoOrbitPermission() {
        return noOrbitPermission;
    }

    public String getOrbitPlusDescription() {
        return orbitPlusDescription;
    }

    public String getOrbitTitlePrefix() {
        return orbitTitlePrefix;
    }

    public String getCanRedeem() {
        return canRedeem;
    }

    public String getQuestsInfo() {
        return questsInfo;
    }

    public String getOrbitInfo() {
        return orbitInfo;
    }

    public String getCannotRedeem() {
        return cannotRedeem;
    }

    public String getRedeemed() {
        return redeemed;
    }

    public String getTierLocked() {
        return tierLocked;
    }

    public String getTierUnlocked() {
        return tierUnlocked;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public boolean debugEnabled() {
        return debug;
    }

    public String getQuestsDailyReset() {
        return questsDailyReset;
    }

    public String getQuestsSeasonReset() {
        return questsSeasonReset;
    }

    public String getTierLevelLocked() {
        return tierLevelLocked;
    }

    public String getTierLevelUnlocked() {
        return tierLevelUnlocked;
    }
}

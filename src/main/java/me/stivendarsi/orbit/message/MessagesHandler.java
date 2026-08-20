package me.stivendarsi.orbit.message;

import org.bukkit.configuration.file.FileConfiguration;

import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class MessagesHandler {
    private String existDialog;
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

    public void load() {
        FileConfiguration config = orbitInstance().getConfig();

        existDialog = config.getString("messages.exist-dialog", "");
        regularName = config.getString("messages.regular-name", "");
        orbitRegularDescription = config.getString("messages.orbit-regular-description", "");
        orbitPlusName = config.getString("messages.orbit-plus-name", "");
        orbitPlusDescription = config.getString("messages.orbit-plus-description", "");
        canRedeem = config.getString("messages.can-redeem", "");
        cannotRedeem = config.getString("messages.cannot-redeem", "");
        redeemed = config.getString("messages.redeemed", "");
        noOrbitPermission = config.getString("messages.no-orbit-permission", "");
        orbitTitlePrefix = config.getString("messages.orbit-title-prefix");

        questsInfo = String.join("<newline>", config.getStringList("messages.quests-info"));

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

    public String getExistDialog() {
        return existDialog;
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

    public String getTierLevelLocked() {
        return tierLevelLocked;
    }

    public String getTierLevelUnlocked() {
        return tierLevelUnlocked;
    }
}

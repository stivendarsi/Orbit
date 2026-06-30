package me.stivendarsi.orbit.experience;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static me.stivendarsi.orbit.Orbit.plugin;

public class QuestHandler {
    private Map<String, Quest> questMap;

    public void load(){
        this.questMap = new HashMap<>();
        ConfigurationSection questsSection = plugin().getConfig().getConfigurationSection("quests");
        if (questsSection == null) return;


        for (String questIdentifier : questsSection.getKeys(false)) {

            Quest quest = new Quest(questIdentifier, questsSection.getConfigurationSection(questIdentifier));
            this.questMap.put(questIdentifier, quest);
        }
    }

    public @Nullable Quest getQuest(String questIdentifier){
        return this.questMap.getOrDefault(questIdentifier, null);
    }

    public Collection<Quest> getQuests(){
        return this.questMap.values();
    }
}

package me.stivendarsi.orbit.orbit.data;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prize {
    private final int levelIndex;
    private final boolean plus;
    private final List<String> description;
    private final String prizeName;
    private final String rewardCommand;
    private String nexoItemId;

    public Prize(int prizeIndex, boolean plus, ConfigurationSection section) {
        this.levelIndex = prizeIndex;
        this.plus = plus;
        if (section.isList("description")) this.description = section.getStringList("description");
        else this.description = Collections.singletonList(section.getString("description"));

        this.prizeName = section.getString("name");
        this.nexoItemId = section.getString("nexo-id", null);
        this.rewardCommand = section.getString("icon-command");
    }


    public int prizeIndex() {
        return levelIndex;
    }

    public boolean plus() {
        return plus;
    }

    public @NotNull Component description() {
        Component text;
        if (description == null || description.isEmpty()) text = Component.empty();
        else
            text = MiniMessage.miniMessage().deserialize(String.join("<newline>", this.description), MiniPlaceholders.audienceGlobalPlaceholders());

        ItemBuilder itemBuilder = NexoItems.itemFromId(this.nexoItemId);
        if (itemBuilder == null) {
            if (text == Component.empty()) return text;
            else return text.appendNewline();
        }

        List<Component> lore = new ArrayList<>();

        lore.add(itemBuilder.getItemName());

        List<Component> itemLore = itemBuilder.getLore();
        if (itemLore != null && !itemLore.isEmpty()) {
            lore.addAll(itemLore);
        }

        return Component.join(JoinConfiguration.newlines(), lore).appendNewline();
    }

    public String name() {
        return prizeName;
    }

    public String getRewardCommand() {
        return rewardCommand;
    }
}

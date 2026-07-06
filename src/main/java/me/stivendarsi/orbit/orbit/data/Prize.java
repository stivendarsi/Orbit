package me.stivendarsi.orbit.orbit.data;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.custom_block.NexoBlockBreakEvent;
import com.nexomc.nexo.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.stivendarsi.orbit.Orbit.plugin;

public class Prize {
    private final int levelIndex;
    private final boolean plus;
    private final List<String> description;
    private final String iconReward;
    private final String rewardCommand;
    private String nexoItemId;

    public Prize(int prizeIndex, boolean plus, ConfigurationSection section) {
        this.levelIndex = prizeIndex;
        this.plus = plus;
        if (section.isList("description")) this.description = section.getStringList("description");
        else this.description = Collections.singletonList(section.getString("description"));

        this.iconReward = section.getString("icon-sprite");
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
        else text = MiniMessage.miniMessage().deserialize(String.join("<newline>", this.description));

        ItemBuilder itemBuilder = NexoItems.itemFromId(this.nexoItemId);
        if (itemBuilder == null) return text;

        List<Component> lore = new ArrayList<>();

        lore.add(itemBuilder.getItemName());

        List<Component> currentLore = itemBuilder.getLore();
        if (currentLore != null && !currentLore.isEmpty()) lore.addAll(currentLore);
        if (text != Component.empty()) {
            lore.add(text);
        }

        return Component.join(JoinConfiguration.newlines(), lore).append(Component.newline());
    }

    public String iconReward() {
        return iconReward;
    }

    public String getRewardCommand() {
        return rewardCommand;
    }
}

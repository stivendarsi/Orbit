package me.stivendarsi.orbit;

import com.nexomc.nexo.glyphs.GlyphTag;
import com.nexomc.nexo.tags.NexoTags;
import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class Constants {
    public static final Sound clickSound = Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.UI, 1, 1);
    public static final Sound pingSound = Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.UI, 1, 1);

    public static boolean runCommandInConsole(Player claimingUser, @Nullable String rewardCommand) {
        if (rewardCommand == null) return false;
        rewardCommand = rewardCommand.replace("<player_name>", claimingUser.getName());

        rewardCommand = PlaceholderAPI.setPlaceholders(claimingUser, rewardCommand);

        String finalRewardCommand = rewardCommand;

        Bukkit.getGlobalRegionScheduler().execute(Orbit.orbitInstance(), () -> {
            orbitInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), finalRewardCommand);
        });

        return true;
    }

    public static Component color(String msg){
        return color(null, msg);
    }

    public static Component color(@Nullable Audience viewer, String msg) {
        return color(viewer, msg, null);
    }

    public static Component color(@Nullable Audience viewer, String msg, @Nullable TagResolver resolver) {
        if (resolver == null) resolver = TagResolver.empty();
        MiniMessage mm = MiniMessage.miniMessage(); // MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build();
        if (viewer == null) return mm.deserialize(msg, MiniPlaceholders.audienceGlobalPlaceholders(), GlyphTag.INSTANCE.getRESOLVER());
        return mm.deserialize(msg, viewer,resolver, MiniPlaceholders.audienceGlobalPlaceholders(), GlyphTag.INSTANCE.getRESOLVER());
    }
}

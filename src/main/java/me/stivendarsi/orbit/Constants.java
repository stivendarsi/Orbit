package me.stivendarsi.orbit;

import com.nexomc.nexo.glyphs.GlyphTag;
import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
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
        orbitInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(claimingUser, rewardCommand));
        return true;
    }

    public static Component color(Player viewer, String msg) {
        return color(viewer, msg, null);
    }

    public static Component color(Player viewer, String msg, @Nullable TagResolver resolver) {
        if (resolver == null) resolver = TagResolver.empty();
        return MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(msg, viewer, MiniPlaceholders.audienceGlobalPlaceholders(), resolver);
    }
}

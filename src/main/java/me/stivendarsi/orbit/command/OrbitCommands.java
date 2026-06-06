package me.stivendarsi.orbit.command;

import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.glyphs.GlyphTag;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import me.stivendarsi.orbit.OrbitMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OrbitCommands {
    public static int open(CommandContext<CommandSourceStack> context) {

        int userExperience = context.getArgument("experience", Integer.class);

        if (!(context.getSource().getExecutor() instanceof Player player)) return 0;

        OrbitMenu orbit = new OrbitMenu(player.getUniqueId(),userExperience);
        orbit.openOrbitMenu();
        return 1;
    }
}
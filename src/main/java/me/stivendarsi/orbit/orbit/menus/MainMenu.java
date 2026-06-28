package me.stivendarsi.orbit.orbit.menus;

import com.google.common.base.Preconditions;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class MainMenu {
    private final UUID userUUID;

    public MainMenu(UUID userUUID) {
        this.userUUID = userUUID;
    }

    public void openMainMenu(Player player) {
        Dialog dialog = Dialog.create(b -> {
            OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
            Preconditions.checkNotNull(currentOrbit, "No current Orbit");

            OrbitMenu orbitMenu = new OrbitMenu(currentOrbit, player.getUniqueId());
            QuestMenu questMenu = new QuestMenu();

            List<Dialog> dialogs = new ArrayList<>();

            dialogs.add(questMenu.getQuestDialog());
            dialogs.add(orbitMenu.getOrbitMenu());


            RegistrySet<Dialog> set = RegistrySet.valueSet(RegistryKey.DIALOG, dialogs);
            DialogType type = DialogType.dialogList(set).build();
            b.empty().type(type).base(DialogBase.builder(Component.text("תפריט ראשי")).build());
        });
        player.showDialog(dialog);
    }
}

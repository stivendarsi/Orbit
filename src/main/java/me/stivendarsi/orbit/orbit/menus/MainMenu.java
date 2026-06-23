package me.stivendarsi.orbit.orbit.menus;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    private final int currentExperience;

    public MainMenu(int currentExperience) {
        this.currentExperience = currentExperience;
    }

    public void openMainMenu(Player player){
        Dialog dialog = Dialog.create(b -> {
            OrbitMenu orbitMenu = new OrbitMenu(this.currentExperience, player.getUniqueId(), LocalDate.of(2026, 8,5).atTime(LocalTime.now()));
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

package me.stivendarsi.orbit.orbit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    private final int currentExperience;

    public MainMenu(int currentExperience) {
        this.currentExperience = currentExperience;
    }

    public void openMainMenu(Player player){
        Dialog dialog = Dialog.create(b -> {
            OrbitMenu orbitMenu = new OrbitMenu(this.currentExperience);
            QuestMenu questMenu = new QuestMenu();

            List<Dialog> dialogs = new ArrayList<>();

            dialogs.add(orbitMenu.getOrbitMenu());
            dialogs.add(questMenu.getQuestDialog());

            RegistrySet<Dialog> set = RegistrySet.keySetFromValues(RegistryKey.DIALOG, dialogs);
            DialogType type = DialogType.dialogList(set).build();
            b.empty().type(type);
        });
        player.showDialog(dialog);
    }
}

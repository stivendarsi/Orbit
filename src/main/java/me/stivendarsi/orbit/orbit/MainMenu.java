package me.stivendarsi.orbit.orbit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import org.bukkit.entity.Player;

public class MainMenu {
    private final int currentExperience;

    public MainMenu(int currentExperience) {
        this.currentExperience = currentExperience;
    }

    public void openMainMenu(Player player){
        Dialog dialog = Dialog.create(b -> {

            DialogType type = DialogType.dialogList()

            b.empty().type();
        });
    }
}

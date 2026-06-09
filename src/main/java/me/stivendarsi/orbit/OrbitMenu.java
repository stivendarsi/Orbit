package me.stivendarsi.orbit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class OrbitMenu {
    private int[] requiredExperience;
    private int currentExperience;
    private int currentIndex;

    public OrbitMenu(int userExperience) {
        this.currentExperience = userExperience;
        this.requiredExperience = new int[100];
        for (int i = 0; i < this.requiredExperience.length; i++) {
            this.requiredExperience[i] = i * 120;
        }
        updateCurrentIndex();
    }

    public void openOrbit(Player player) {
        Dialog dialog = Dialog.create(builder -> {
            DialogType buttons = getPageButtons(getUserPage());
            builder.empty().type(buttons);
        });

        player.showDialog(dialog);
    }

    public MultiActionType getPageButtons(int page) {
        List<ActionButton> actionButtons = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        for (int levelIndex = min; levelIndex < max; levelIndex++) {
            Component tierComponent;

            if (isLevelIndexUnLocked(levelIndex)) tierComponent = Component.text(levelIndex + "\uD83D\uDD12");
            else tierComponent = Component.text(levelIndex + "\uD83D\uDD13");

            ActionButton actionButton = ActionButton.create(tierComponent, null,30, null);
            actionButtons.add(actionButton);
        }

        return DialogType.multiAction(actionButtons).columns(10).build();
    }

    private int getUserPage(){
        return this.currentIndex % 10;
    }

    private boolean isLevelIndexUnLocked(int levelIndex){
        int xp = this.requiredExperience[levelIndex];
        return xp <= this.currentExperience;
    }

    private void updateCurrentIndex() {
        for (int i = 0; i < this.requiredExperience.length; i++) {
            int nextLevelExperience = this.requiredExperience[i + 1];

            if (this.currentExperience < nextLevelExperience) {
                this.currentIndex = i;
                return;
            }
        }
        this.currentIndex = this.requiredExperience[this.requiredExperience.length - 1];
    }
}

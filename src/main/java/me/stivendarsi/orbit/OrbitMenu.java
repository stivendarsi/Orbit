package me.stivendarsi.orbit;

import com.nexomc.nexo.glyphs.GlyphTag;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OrbitMenu {
    private final int[] requiredExperience;
    private final int currentExperience;
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
            List<DialogBody> bodies = new ArrayList<>();

            bodies.add(getProgressBar());

            builder.empty().type(buttons).base(DialogBase.builder(Component.text("מסלול  התקדמות")).body(bodies).build());
        });

        player.showDialog(dialog);
    }

    public DialogBody getProgressBar() {
        StringBuilder b = new StringBuilder();

        int len = this.requiredExperience.length;

        int nextLvlXp = len <= this.currentIndex + 1 ? this.requiredExperience[len - 1] : this.requiredExperience[this.currentIndex + 1];

        int progress = (int) (((nextLvlXp - this.currentExperience) / (double) nextLvlXp) * 100);
        int left = 100 - progress;


        b.append("<green>");

        for (int i = 0; i < progress; i++) {
            if (i == 0) b.append("···");
            else if (i == progress - 1 && left == 0) b.append("→");
            else {
                b.append("‑");
                b.append("<shift:-1>");
            }
        }

        b.append("</green>");
        b.append("<dark_gray>");
        for (int i = 0; i < left; i++) {
            if (i == 0 && progress == 0) b.append("···");
            else if (i == left - 1) b.append("→");
            else {
                b.append("‑");
                b.append("<shift:-1>");
            }
        }

        b.append("<reset>");

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(b.toString()), 600);
    }

    public MultiActionType getPageButtons(int page) {
        List<ActionButton> actionButtons = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        for (int levelIndex = min; levelIndex < max; levelIndex++) {
            Component tierComponent;

            if (isLevelIndexUnLocked(levelIndex)) tierComponent = Component.text(levelIndex + "\uD83D\uDD12");
            else tierComponent = Component.text(levelIndex + "\uD83D\uDD13");

            ActionButton actionButton = ActionButton.create(tierComponent, null, 30, null);
            actionButtons.add(actionButton);
        }

        return DialogType.multiAction(actionButtons).columns(10).build();
    }

    private int getUserPage() {
        return this.currentIndex % 10;
    }

    private boolean isLevelIndexUnLocked(int levelIndex) {
        int xp = this.requiredExperience[levelIndex];
        return xp <= this.currentExperience;
    }

    private void updateCurrentIndex() {
        for (int i = 0; i < this.requiredExperience.length && isLevelIndexUnLocked(i); i++) {
            this.currentIndex = i;
        }
    }
}

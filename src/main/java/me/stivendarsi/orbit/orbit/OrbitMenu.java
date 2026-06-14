package me.stivendarsi.orbit.orbit;

import com.nexomc.nexo.glyphs.GlyphTag;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
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
            this.requiredExperience[i] = i * 100;
        }
        updateCurrentIndex();
    }

    public Dialog getOrbitMenu(Player player) {
        Dialog dialog = Dialog.create(builder -> {
            DialogType buttons = getPageButtons(getUserPage(), false);
            List<DialogBody> bodies = new ArrayList<>();
            bodies.add(getDoneText());
            bodies.add(getProgressBar());

            builder.empty().type(buttons).base(DialogBase.builder(Component.text("מסלול  התקדמות")).body(bodies).build());
        });

        player.showDialog(dialog);

        return dialog;
    }

    private DialogBody getDoneText(){
        int len = this.requiredExperience.length;
        int nextLvlXp = this.currentIndex + 1 >= len ? this.requiredExperience[len-1] : this.requiredExperience[currentIndex + 1];

        StringBuilder b = new StringBuilder();
        b.append("<gradient:#ff8cec:#ff54c3>").append(this.currentExperience).append("⭐ / ").append(nextLvlXp).append("⭐</gradient:#ff8cec:#ff54c3>");

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(b.toString()), 500);
    }

    private DialogBody getProgressBar() {
        StringBuilder b = new StringBuilder();

        int len = this.requiredExperience.length;
        int currentLvlXp = this.requiredExperience[currentIndex];
        int nextLvlXp = this.currentIndex + 1 >= len
                ? this.requiredExperience[len - 1]
                : this.requiredExperience[currentIndex + 1];

        int levelXpRange = nextLvlXp - currentLvlXp;
        int xpGainedInLevel = this.currentExperience - currentLvlXp;

        int percentDone;

        if (levelXpRange <= 0) percentDone = 100;
        else percentDone = (int) (((double) xpGainedInLevel / levelXpRange) * 100);


        percentDone = Math.max(0, Math.min(100, percentDone));

        int percentLeft = 100 - percentDone;
        System.out.println("progress: " + percentDone);
        System.out.println("left: " + percentLeft);

        b.append("<white>").append(this.currentIndex).append(" ");

        b.append("<gradient:#b2f7c1:#08ff3d>");

        for (int i = 0; i < percentDone; i++) {
            if (i == 0) b.append("···");
            else if (i == percentDone - 1 && percentLeft == 0) b.append("→");
            else {
                b.append("‑");
                b.append("<shift:-1>");
            }
        }

        b.append("<dark_gray>");
        for (int i = 0; i < percentLeft; i++) {
            if (i == 0 && percentDone == 0) b.append("···");
            else if (i == percentLeft - 1) b.append("→");
            else {
                b.append("‑");
                b.append("<shift:-1>");
            }
        }

        b.append("</dark_gray>");
        b.append("</gradient:#b2f7c1:#08ff3d>");
        if (this.currentIndex + 1 < len) b.append(" ").append(this.currentIndex+1);

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(b.toString()), 500);
    }

    private MultiActionType getPageButtons(int page, boolean plus) {
        List<ActionButton> actionButtons = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        ActionButton type;
        if (plus) type = ActionButton.create(Component.text("רגיל"), Component.text("מסלול שפתוח לכל השחקנים"), 30, null);

        for (int levelIndex = min; levelIndex < max; levelIndex++) {
            Component tierComponent;

            System.out.println("level index: " + levelIndex);
            System.out.println("page: " + page);

            if (isLevelIndexUnLocked(levelIndex)) tierComponent = Component.text(levelIndex + "\uD83D\uDD13");
            else tierComponent = Component.text(levelIndex + "\uD83D\uDD12");

            ActionButton actionButton = ActionButton.create(tierComponent, Component.text(levelIndex), 30, null);
            actionButtons.add(actionButton);
        }


        ActionButton nextPage = ActionButton.create(Component.text("הבא"), null, 60, DialogAction.customClick((response, audience) -> {

        }, ClickCallback.Options.builder().build()));

        return DialogType.multiAction(actionButtons).columns(10).build();
    }

    private int getUserPage() {
        return this.currentIndex / 10;
    }

    private boolean isLevelIndexUnLocked(int levelIndex) {
        int xp = this.requiredExperience[levelIndex];

        System.out.println("Requiered: " + xp);
        System.out.println("Has: " + this.currentIndex);

        return xp <= this.currentExperience;
    }

    private void updateCurrentIndex() {
        for (int i = 0; i < this.requiredExperience.length && isLevelIndexUnLocked(i); i++) {
            this.currentIndex = i;
        }
    }
}

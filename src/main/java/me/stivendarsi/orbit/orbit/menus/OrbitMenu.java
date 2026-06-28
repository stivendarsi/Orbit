package me.stivendarsi.orbit.orbit.menus;

import com.google.common.base.Preconditions;
import com.nexomc.nexo.glyphs.GlyphTag;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.orbit.data.Prize;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

@SuppressWarnings("UnstableApiUsage")
public class OrbitMenu {
    private final int[] requiredExperience;
    private int currentIndex;
    private final UUID user;
    private final OrbitData orbitData;

    public OrbitMenu(OrbitData orbitData, UUID user) {
        this.user = user;
        this.orbitData = orbitData;
        this.requiredExperience = new int[orbitData.tierAmount()];
        for (int i = 0; i < this.requiredExperience.length; i++) {
            this.requiredExperience[i] = i * orbitData.levelMultiplier();
        }
        updateCurrentIndex();
    }

    public Dialog getOrbitMenu() {
        return getPage(getUserPage());
    }

    private Dialog getPage(int page) {
        Dialog dialog = Dialog.create(builder -> {
            DialogType buttons = getPageType(page);
            List<DialogBody> bodies = new ArrayList<>();
            bodies.add(getOrbitEndText());
            bodies.add(getDoneText());
            bodies.add(getProgressBar());


            Component title = MiniMessage.miniMessage().deserialize("מסלול התקדמות <head:%s>".formatted(this.user));

            builder.empty().type(buttons).base(DialogBase.builder(title).pause(false).afterAction(DialogBase.DialogAfterAction.NONE).externalTitle(Component.text("מסלול התקדמות")).body(bodies).build());
        });
        return dialog;
    }

    private DialogBody getOrbitEndText() {
        String time = this.orbitData.end().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Component text = MiniMessage.miniMessage().deserialize("<gradient:#ff771c:#ffe4c7>המסלול יסתיים ב-</gradient><gradient:#ff771c:#ffe4c7>" + time + "</gradient>");
        return DialogBody.plainMessage(text, 300);
    }

    private DialogBody getDoneText() {
        int len = this.requiredExperience.length;
        int nextLvlXp = this.currentIndex + 1 >= len ? this.requiredExperience[len - 1] : this.requiredExperience[currentIndex + 1];


        LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
        Preconditions.checkNotNull(localUserData, "Null user data");

        StringBuilder b = new StringBuilder();
        b.append("<gradient:#ff8cec:#ff54c3>").append(localUserData.getUserExperience(this.orbitData.identifier())).append("⭐ / ").append(nextLvlXp).append("⭐</gradient:#ff8cec:#ff54c3>");

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(b.toString()), 200);
    }

    private DialogBody getProgressBar() {
        LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
        Preconditions.checkNotNull(localUserData, "Null user data");


        StringBuilder b = new StringBuilder();

        int len = this.requiredExperience.length;
        int currentLvlXp = this.requiredExperience[currentIndex];
        int nextLvlXp = this.currentIndex + 1 >= len
                ? this.requiredExperience[len - 1]
                : this.requiredExperience[currentIndex + 1];

        int levelXpRange = nextLvlXp - currentLvlXp;
        int xpGainedInLevel = localUserData.getUserExperience(this.orbitData.identifier()) - currentLvlXp;

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
        if (this.currentIndex + 1 < len) b.append(" ").append(this.currentIndex + 1);

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(b.toString()), 500);
    }

    private MultiActionType getPageType(int page) {

        List<ActionButton> buttons = new ArrayList<>();

        ActionButton corn = ActionButton.builder(Component.text("רמה\\מסלול")).width(60).build();
        buttons.add(corn);

        buttons.addAll(getLevelNumbers(page));

        buttons.addAll(getPageButtons(page, false));
        buttons.addAll(getPageButtons(page, true));


        if (0 <= page - 1) {
            ActionButton prevPage = ActionButton.create(Component.text("קודם"), null, 90, DialogAction.customClick((response, audience) -> {
                if (!(audience instanceof Player player)) return;
                player.showDialog(getPage(page - 1));
            }, ClickCallback.Options.builder().build()));
            buttons.add(prevPage);
        }

        if (page + 1 < this.requiredExperience.length / 10) {
            ActionButton nextPage = ActionButton.create(Component.text("הבא"), null, 90, DialogAction.customClick((response, audience) -> {
                if (!(audience instanceof Player player)) return;
                player.showDialog(getPage(page + 1));
            }, ClickCallback.Options.builder().build()));

            buttons.add(nextPage);
        }

        return DialogType.multiAction(buttons).columns(11).build();
    }

    private List<ActionButton> getLevelNumbers(int page) {
        List<ActionButton> levels = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        MiniMessage mm = MiniMessage.miniMessage();

        for (int levelIndex = min; levelIndex < max; levelIndex++) {
            Component tierComponent;
            Component status = mm.deserialize("<gray>רמה: <white>" + levelIndex);

            if (isLevelIndexUnLocked(levelIndex)) {
                tierComponent = mm.deserialize(levelIndex + " <green>" + Constants.unLocked);
                status = status.append(mm.deserialize("<newline><green> פתוח" + Constants.unLocked));
            } else {
                tierComponent = mm.deserialize(levelIndex + " <red>" + Constants.locked);
                status = status.append(mm.deserialize("<newline><red>נעול " + Constants.locked));
            }

            ActionButton actionButton = ActionButton.create(tierComponent, status, 30, null);
            levels.add(actionButton);
        }
        return levels;
    }

    private List<ActionButton> getPageButtons(int page, boolean plus) {
        List<ActionButton> actionButtons = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        ActionButton type;

        if (plus)
            type = ActionButton.create(Component.text("מתקדם +"), Component.text("מסלול שפתוח למנויים בלבד."), 60, DialogAction.customClick((response, audience) -> {
                if (!(audience instanceof Player player)) return;
                player.performCommand("store");
            }, ClickCallback.Options.builder().build()));
        else type = ActionButton.create(Component.text("רגיל"), Component.text("מסלול שפתוח לכל השחקנים."), 60, null);


        actionButtons.add(type);
        MiniMessage mm = MiniMessage.miniMessage();

        for (int prizeIndex = min; prizeIndex < max; prizeIndex++) {
            Prize prize = orbitData.getPrize(prizeIndex, plus);


            System.out.println("level index: " + prizeIndex);
            System.out.println("page: " + page);

            boolean isPrizeUnlocked = isLevelIndexUnLocked(prizeIndex);
            boolean isPrizeTaken = isPrizeAvailable(prizeIndex, plus);

            Component toolTip = Component.empty();
            Component tierText = Component.empty();

            if (prize != null) {
                tierText = MiniMessage.miniMessage().deserialize("<sprite:%s".formatted(prize.iconReward()));
                toolTip = mm.deserialize(String.join("<newline>", prize.description()));
            }


            if (!isPrizeUnlocked) toolTip = toolTip.append(mm.deserialize("<red>אין אפשרת לקחת</red>"));
            else if (isPrizeTaken) toolTip = toolTip.append(mm.deserialize("<dark_gray>נלקח</dark_gray>"));
            else toolTip = toolTip.append(mm.deserialize("<green>ניתן לקחת</green>"));

            ActionButton actionButton = ActionButton.create(tierText, toolTip, 30, DialogAction.customClick((dialogResponseView, audience) -> {
                if (prize == null || !isPrizeUnlocked || isPrizeTaken) return;

                LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
                Preconditions.checkNotNull(localUserData, "Null user data");

                localUserData.takePrize(orbitData.identifier(), prize.prizeIndex(), plus);

            }, ClickCallback.Options.builder().build()));
            actionButtons.add(actionButton);
        }

        return actionButtons;
    }


    private int getUserPage() {
        return this.currentIndex / 10;
    }

    private boolean isPrizeAvailable(int prizeIndex, boolean plus) {
        LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
        Preconditions.checkNotNull(localUserData, "Null user data");

        Pair<boolean[], boolean[]> prizeData = localUserData.getTiersData(this.orbitData.identifier());
        if (prizeData == null) return false;

        if (plus) return prizeData.getRight()[prizeIndex];
        else return prizeData.getLeft()[prizeIndex];
    }

    private boolean isLevelIndexUnLocked(int levelIndex) {
        LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
        Preconditions.checkNotNull(localUserData, "Null user data");
        int xp = this.requiredExperience[levelIndex];

        System.out.println("Requiered: " + xp);
        System.out.println("Has: " + this.currentIndex);

        return xp <= localUserData.getUserExperience(this.orbitData.identifier());
    }

    private void updateCurrentIndex() {
        for (int i = 0; i < this.requiredExperience.length && isLevelIndexUnLocked(i); i++) {
            this.currentIndex = i;
        }
    }
}

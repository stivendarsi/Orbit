package me.stivendarsi.orbit.orbit.menus;

import com.google.common.base.Preconditions;
import com.nexomc.nexo.glyphs.GlyphTag;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.message.MessagesHandler;
import me.stivendarsi.orbit.orbit.data.LocalUserData;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.orbit.data.PrizeData;
import me.stivendarsi.orbit.quest.QuestHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

@SuppressWarnings("UnstableApiUsage")
public class OrbitMenu {
    private final int[] requiredExperience;
    private int currentIndex;
    private final Player viewer;
    private final UUID user;
    private final OrbitData orbitData;

    public OrbitMenu(OrbitData orbitData, Player viewer) {
        this.user = viewer.getUniqueId();
        this.viewer = viewer;
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

            TagResolver tagResolver = TagResolver.builder().tag("player_uuid", Tag.preProcessParsed(String.valueOf(this.user))).build();

            Component title = MiniMessage.miniMessage().deserialize(this.orbitData.title(), tagResolver);

            builder.empty()
                    .type(buttons)
                    .base(DialogBase.builder(title)
                            .pause(false)
                            .afterAction(DialogBase.DialogAfterAction.NONE)
                            .externalTitle(Component.text("מסלול התקדמות ✔")).body(bodies).build());
        });
        return dialog;
    }

    private DialogBody getOrbitEndText() {
        String date = this.orbitData.end().format(DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(QuestHandler.ISRAEL_ZONE_ID));
        String time = this.orbitData.end().format(DateTimeFormatter.ofPattern("HH:mm").withZone(QuestHandler.ISRAEL_ZONE_ID));
        Component text = Constants.color("<gradient:#ff771c:#ffe4c7>המסלול יסתיים ב-</gradient><gradient:#ff771c:#ffe4c7>" + date + " בשעה " + time + "</gradient>");
        return DialogBody.plainMessage(text, 300);
    }

    private DialogBody getDoneText() {
        int len = this.requiredExperience.length;
        int requiredStars = this.currentIndex + 1 >= len ? this.requiredExperience[len - 1] : this.requiredExperience[currentIndex + 1];


        LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
        Preconditions.checkNotNull(localUserData, "Null user data");


        String msg = "<cut_progress_pink:'⭐<orbit_stars> / ⭐<required_stars>'>";

        TagResolver experienceResolver = TagResolver.builder()
                .tag("required_stars", Tag.preProcessParsed(String.valueOf(NumberFormat.getNumberInstance().format(requiredStars))))
                .build();

        Component msgComponent = Constants.color(this.viewer, msg, experienceResolver);

        return DialogBody.plainMessage(msgComponent, 200);
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

        b.append("<white>").append(this.currentIndex + 1).append(" ");

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
        if (this.currentIndex + 1 < len) b.append(" ").append(this.currentIndex + 1 + 1);

        Component progressBarComponent = MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(b.toString());

        return DialogBody.plainMessage(progressBarComponent, 400);
    }

    private MultiActionType getPageType(int page) {

        List<ActionButton> buttons = new ArrayList<>();

        ActionButton corn = ActionButton.builder(Component.text("רמה\\מסלול")).width(60).build();
        buttons.add(corn);

        buttons.addAll(getLevelNumbers(page));

        buttons.addAll(getPageButtons(page, false));
        buttons.addAll(getPageButtons(page, true));


        if (0 <= page - 1) {
            ActionButton prevPage = ActionButton.create(Constants.color(viewer, mainHandler().messagesHandler().getPreviousPage()), null, 90, DialogAction.customClick((response, audience) -> {
                if (!(audience instanceof Player player)) return;
                player.showDialog(getPage(page - 1));
            }, ClickCallback.Options.builder().build()));
            buttons.add(prevPage);
        }


        if (page + 1 < this.requiredExperience.length / 10) {
            ActionButton nextPage = ActionButton.create(Constants.color(viewer, mainHandler().messagesHandler().getNextPage()), null, 90, DialogAction.customClick((response, audience) -> {
                if (!(audience instanceof Player player)) return;
                player.showDialog(getPage(page + 1));
            }, ClickCallback.Options.builder().build()));

            buttons.add(nextPage);
        }

        ActionButton exist = ActionButton.create(Constants.color(this.viewer, mainHandler().messagesHandler().getBackButton()), null, 100, DialogAction.customClick((response, audience) -> {
            MainMenu.openMainMenu(this.viewer);
        }, ClickCallback.Options.builder().build()));

        return DialogType.multiAction(buttons).exitAction(exist).columns(11).build();
    }

    private List<ActionButton> getLevelNumbers(int page) {
        List<ActionButton> levels = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        //   MiniMessage mm = MiniMessage.miniMessage();
        MessagesHandler mh = mainHandler().messagesHandler();

        for (int levelIndex = min; levelIndex < max; levelIndex++) {

            String levelLockedStatus;
            if (isLevelIndexUnLocked(levelIndex)) levelLockedStatus = mh.getTierUnlocked();
            else levelLockedStatus = mh.getTierLocked();

            TagResolver levelResolver = TagResolver.builder()
                    .tag("level", Tag.preProcessParsed(String.valueOf(levelIndex + 1)))
                    .tag("level_status", Tag.preProcessParsed(levelLockedStatus))
                    .build();


            Component tierComponent = Constants.color(this.viewer, mh.getLevelTitle(), levelResolver);
            Component status = Constants.color(this.viewer, mh.getLevelDescription(), levelResolver).appendNewline();

            if (isLevelIndexUnLocked(levelIndex))
                status = status.append(Constants.color(this.viewer, mh.getTierLevelUnlocked(), levelResolver));
            else
                status = status.append(Constants.color(this.viewer, mh.getTierLevelLocked(), levelResolver));


            ActionButton actionButton = ActionButton.create(tierComponent, status, 35, null);
            levels.add(actionButton);
        }
        return levels;
    }

    private List<ActionButton> getPageButtons(int page, boolean plus) {
        List<ActionButton> actionButtons = new ArrayList<>();
        int min = page * 10;
        int max = min + 10;

        ActionButton type;

        MiniMessage mm = MiniMessage.miniMessage();
        MessagesHandler mh = mainHandler().messagesHandler();

        if (plus)
            type = ActionButton.create(mm.deserialize(mh.getOrbitPlusName()), mm.deserialize(mh.getOrbitPlusDescription(), this.viewer, MiniPlaceholders.audienceGlobalPlaceholders()), 60, DialogAction.customClick((response, audience) -> {
                if (!(audience instanceof Player player)) return;
                player.performCommand("store");
            }, ClickCallback.Options.builder().build()));
        else
            type = ActionButton.create(mm.deserialize(mh.getRegularName()), mm.deserialize(mh.getOrbitRegularDescription(), this.viewer, MiniPlaceholders.audienceGlobalPlaceholders()), 60, null);

        actionButtons.add(type);

        for (int prizeIndex = min; prizeIndex < max; prizeIndex++) {

            PrizeData prizeData = orbitData.getPrize(prizeIndex, plus);
            Permission orbitPermission = mainHandler().orbitHandler().getOrbitPermission(this.orbitData.identifier());
            Preconditions.checkNotNull(orbitPermission, "Null orbit permission");

            boolean isLevelUnlocked = isLevelIndexUnLocked(prizeIndex);
            boolean isPrizeTaken = isPrizeAvailable(prizeIndex, plus);

            boolean userHasAccess = plus ? this.viewer.hasPermission(orbitPermission) : true;

            Component toolTip = Component.empty();
            Component tierText = Component.empty();

            if (prizeData != null) {
                tierText = MiniMessage.miniMessage().deserialize(prizeData.name(), viewer, MiniPlaceholders.audienceGlobalPlaceholders());
                toolTip = prizeData.description(viewer);

                if (isPrizeTaken) tierText = tierText.color(NamedTextColor.DARK_GRAY);
            }

            if (!userHasAccess) {
                toolTip = toolTip.append(Constants.color(this.viewer, mh.getNoOrbitPermission()));
            } else if (!isLevelUnlocked) {
                toolTip = toolTip.append(Constants.color(this.viewer, mh.getCannotRedeem()));
            } else if (!isPrizeTaken) {
                toolTip = toolTip.append(Constants.color(this.viewer, mh.getCanRedeem()));
            } else {
                toolTip = toolTip.append(Constants.color(this.viewer, mh.getRedeemed()));
            }


            ActionButton actionButton = ActionButton.create(tierText, toolTip, 35, DialogAction.customClick((dialogResponseView, audience) -> {
                if (prizeData == null || !isLevelUnlocked || isPrizeTaken || !userHasAccess) return;

                LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
                Preconditions.checkNotNull(localUserData, "Null user data");

                localUserData.takePrize(orbitData.identifier(), prizeData.prizeIndex(), plus);
                audience.showDialog(getPage(page));

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

        Pair<BitSet, BitSet> prizeData = localUserData.getTiersData(this.orbitData.identifier());
        if (prizeData == null) return false;

        if (plus) return prizeData.getRight().get(prizeIndex);
        else return prizeData.getLeft().get(prizeIndex);
    }

    private boolean isLevelIndexUnLocked(int levelIndex) {
        LocalUserData localUserData = mainHandler().userHandler().getUser(this.user);
        Preconditions.checkNotNull(localUserData, "Null user data");
        int xp = this.requiredExperience[levelIndex];


        return xp <= localUserData.getUserExperience(this.orbitData.identifier());
    }

    private void updateCurrentIndex() {
        for (int i = 0; i < this.requiredExperience.length && isLevelIndexUnLocked(i); i++) {
            this.currentIndex = i;
        }
    }
}

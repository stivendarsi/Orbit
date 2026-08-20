package me.stivendarsi.orbit.orbit.menus;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import me.stivendarsi.orbit.quest.QuestData;
import me.stivendarsi.orbit.quest.enums.QuestType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.DurationUtils;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class QuestMenu {
    private final UUID userUUID;
    private final Player viewer;

    public QuestMenu(Player viewer) {
        this.userUUID = viewer.getUniqueId();
        this.viewer = viewer;
    }

    public Dialog getQuestDialog() {
        ActionButton backButton = ActionButton.builder(Constants.color(viewer, "<red>חזרה"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (!(audience instanceof Player player)) return;
                    MainMenu.openMainMenu(player);
                }))).width(100)
                .build();

        Dialog questDialog = Dialog.create(b -> {
            b.empty()
                    .type(DialogType.notice(backButton))
                    .base(DialogBase.builder(Component.text("משימות"))
                            .body(getBody())
                            .externalTitle(Component.text("משימות"))
                            .pause(false)
                            .afterAction(DialogBase.DialogAfterAction.NONE)
                            .build()
                    );
        });

        return questDialog;
    }


    private List<DialogBody> getBody() {
        MiniMessage mm = MiniMessage.miniMessage();
        DialogBody dailyQuestsTitle = DialogBody.plainMessage(mm.deserialize("<u><gradient:#2a94f7:#63cbff:#2a94f7>משימות יומיות</gradient:#2a94f7:#63cbff:#2a94f7></u>"));
        DialogBody seasonQuestsTitle = DialogBody.plainMessage(mm.deserialize("<u><gradient:#e37602:#ffd500:#e37602>משימות עונתיות</gradient:#e37602:#ffd500:#e37602></u>"));


        DialogBody questInfo = DialogBody.plainMessage(mm.deserialize(mainHandler().messagesHandler().getQuestsInfo()));

        List<DialogBody> bodies = new ArrayList<>();

        bodies.add(questInfo);

        bodies.add(dailyQuestsTitle);

        mainHandler().questHandler().dailyQuests().forEach(quest -> {
            bodies.add(getQuestBlock(quest, quest.getUserCount(userUUID)));
        });

        OrbitData currentOrbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbitData == null) return bodies;

        bodies.add(seasonQuestsTitle);

        currentOrbitData.seasonQuests().forEach(quest -> {
            bodies.add(getQuestBlock(quest, quest.getUserCount(userUUID)));
        });
        return bodies;
    }

    private DialogBody getQuestBlock(QuestData questData, int completed) {
        List<String> builder = new ArrayList<>();

        boolean questCompleted = questData.requiredAmount() <= completed;

        builder.add("<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>" + questData.description() + "</gradient:#fabf1b:#faea3c:#fabf1b> ⏴</gray>");

        if (questData.questType() == QuestType.PLAY_TIME) {
            Duration completedDuration = Duration.ofMinutes(questData.getUserCount(userUUID)).plus(questData.currentSessionTimePlayed(userUUID));
            // String completedTimeString = DurationFormatUtils.formatDuration(Duration.ofMinutes(questData.getUserCount(userUUID)).toMillis() + questData.currentSessionTimePlayed(userUUID).toMillis(), "dd:HH:mm");
            String a = DurationFormatUtils.formatDurationWords(completedDuration.toMillis(), true, true);

            a = a.replace(" day ", "ימים");
            a = a.replace(" days ", "ימים");

            a = a.replace(" hour ", "שעות");
            a = a.replace(" hours ", "שעות");

            a = a.replace(" minute ", "דקות");
            a = a.replace(" minutes ", "דקות");

            a = a.replace(" second ", "דקות");
            a = a.replace(" seconds ", "דקות");

            builder.add("<#fffb00>" + a + "</#fffb00>");
        } else
            builder.add("<#fffb00>" + NumberFormat.getNumberInstance().format(completed) + "/" + NumberFormat.getNumberInstance().format(questData.requiredAmount()) + "</#fffb00>");

        if (questCompleted)
            builder.add("<gradient:#07ba55:#32f02b:#07ba55>☑ הושלם</gradient:#07ba55:#32f02b:#07ba55>");
        else builder.add("<gradient:#e32a05:#ff4000:#e32a05>☐ עדיין לא הושלם</gradient:#e32a05:#ff4000:#e32a05>");

        builder.add("<gradient:#d56cf5:#f59cff:#d56cf5>◆ " + questData.rewardDescription() + " ◆</gradient:#d56cf5:#f59cff:#d56cf5>");

        Component text = MiniMessage.miniMessage().deserialize(String.join("<newline>", builder));
        return DialogBody.item(questData.questIcon()).description(DialogBody.plainMessage(text, questData.descriptionWidth())).showTooltip(false).build();
    }
}

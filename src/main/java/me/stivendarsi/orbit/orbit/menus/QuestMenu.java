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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class QuestMenu {
    private final UUID userUUID;

    private static DialogBody dailyQuestsTitle;
    private static DialogBody seasonQuestsTitle;
    private static DialogBody questInfo;
    private final Player viewer;

    public static void loadStaticBlocks() {
        dailyQuestsTitle = DialogBody.plainMessage(Constants.color("<u><gradient:#2a94f7:#63cbff:#2a94f7>משימות יומיות</gradient:#2a94f7:#63cbff:#2a94f7></u>"));
        seasonQuestsTitle = DialogBody.plainMessage(Constants.color("<u><gradient:#e37602:#ffd500:#e37602>משימות עונתיות</gradient:#e37602:#ffd500:#e37602></u>"));
        questInfo = DialogBody.plainMessage(Constants.color(mainHandler().messagesHandler().getQuestsInfo()));
    }

    public QuestMenu(Player viewer) {
        this.userUUID = viewer.getUniqueId();
        this.viewer = viewer;
    }

    public Dialog getQuestDialog() {

        ActionButton backButton = ActionButton.builder(Constants.color(viewer, mainHandler().messagesHandler().getBackButton()))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (!(audience instanceof Player player)) return;
                    MainMenu.openMainMenu(player);
                }))).width(100)
                .build();

        Dialog questDialog = Dialog.create(b -> {
            b.empty()
                    .type(DialogType.notice(backButton))
                    .base(DialogBase.builder(Component.text("משימות ⭐"))
                            .body(getBody())
                            .pause(false)
                            .afterAction(DialogBase.DialogAfterAction.NONE)
                            .build()
                    );
        });

        return questDialog;
    }


    private List<DialogBody> getBody() {
        List<DialogBody> bodies = new ArrayList<>();

        bodies.add(questInfo);

        bodies.add(dailyQuestsTitle);

        mainHandler().questHandler().dailyQuests().forEach(quest -> {
            bodies.add(getQuestBlock(quest, quest.getUserProgress(userUUID)));
        });

        OrbitData currentOrbitData = mainHandler().orbitHandler().getCurrentOrbit();
        if (currentOrbitData == null) return bodies;

        bodies.add(seasonQuestsTitle);

        currentOrbitData.seasonQuests().forEach(quest -> {
            bodies.add(getQuestBlock(quest, quest.getUserProgress(userUUID)));
        });
        return bodies;
    }

    private DialogBody getQuestBlock(QuestData questData, int completed) {
        List<String> builder = new ArrayList<>();

        boolean questCompleted = questData.requiredAmount() <= completed;

        builder.add("<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>" + questData.description() + "</gradient:#fabf1b:#faea3c:#fabf1b> ⏴</gray>");
        builder.add("<#fffb00>" + NumberFormat.getNumberInstance().format(completed) + "/" + NumberFormat.getNumberInstance().format(questData.requiredAmount()) + "</#fffb00>");

        if (questCompleted)
            builder.add("<gradient:#07ba55:#32f02b:#07ba55>☑ הושלם</gradient:#07ba55:#32f02b:#07ba55>");
        else builder.add("<gradient:#e32a05:#ff4000:#e32a05>☐ עדיין לא הושלם</gradient:#e32a05:#ff4000:#e32a05>");

        builder.add("<gradient:#d56cf5:#f59cff:#d56cf5>◆ " + questData.rewardDescription() + " ◆</gradient:#d56cf5:#f59cff:#d56cf5>");

        Component text = Constants.color(String.join("<newline>", builder));
        return DialogBody.item(questData.questIcon()).description(DialogBody.plainMessage(text, questData.descriptionWidth())).showTooltip(false).build();
    }
}

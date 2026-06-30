package me.stivendarsi.orbit.orbit.menus;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;

public class QuestMenu {
    public Dialog getQuestDialog() {
        Dialog questDialog = Dialog.create(b -> {
            b.empty()
                    .type(DialogType.notice())
                    .base(DialogBase.builder(Component.text("משימות")).body(getBody())
                            .externalTitle(Component.text("משימות")).build());
        });

        return questDialog;
    }


    private List<DialogBody> getBody() {
        MiniMessage mm = MiniMessage.miniMessage();
        DialogBody dailyQuests = DialogBody.plainMessage(mm.deserialize("<u><gradient:#2a94f7:#63cbff:#2a94f7>משימות יומיות</gradient:#2a94f7:#63cbff:#2a94f7></u>"), 400);


        DialogBody dailyQuest1 = DialogBody.plainMessage(mm.deserialize("" +
                "<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>להרוג שחקנים שונים</gradient:#fabf1b:#faea3c:#fabf1b> ⏴<newline>" +
                "<#fffb00>25/100</#fffb00><newline>" +
                "<gradient:#e32a05:#ff4000:#e32a05>☐ עדיין לא הושלם</gradient:#e32a05:#ff4000:#e32a05><newline>" +
                "<gradient:#d56cf5:#f59cff:#d56cf5>◆ 120 כוכבים ◆</gradient:#d56cf5:#f59cff:#d56cf5>"
        ));

        DialogBody dailyQuest2 = DialogBody.plainMessage(mm.deserialize("" +
                "<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>לחצוב 50 בלוקים</gradient:#fabf1b:#faea3c:#fabf1b> ⏴<newline>" +
                "<#fffb00>59/100</#fffb00><newline>" +
                "<gradient:#07ba55:#32f02b:#07ba55>☑ הושלם</gradient:#07ba55:#32f02b:#07ba55><newline>" +
                "<gradient:#d56cf5:#f59cff:#d56cf5>◆ 120 כוכבים ◆</gradient:#d56cf5:#f59cff:#d56cf5>"
        ));

        DialogBody dailyQuest3 = DialogBody.plainMessage(mm.deserialize("" +
                "<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>לקנות 1000 אייטמים מהחנות</gradient:#fabf1b:#faea3c:#fabf1b> ⏴<newline>" +
                "<#fffb00>6/100</#fffb00><newline>" +
                "<gradient:#07ba55:#32f02b:#07ba55>☑ הושלם</gradient:#07ba55:#32f02b:#07ba55><newline>"+
                "<gradient:#d56cf5:#f59cff:#d56cf5>◆ 120 כוכבים ◆</gradient:#d56cf5:#f59cff:#d56cf5>"
        ));


        PlainMessageDialogBody dailyQuest4text = DialogBody.plainMessage(mm.deserialize("" +
                "<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>לקנות 1000 אייטמים מהחנות</gradient:#fabf1b:#faea3c:#fabf1b> ⏴<newline>" +
                "<#fffb00>6/100</#fffb00><newline>" +
                "<gradient:#07ba55:#32f02b:#07ba55>☑ הושלם</gradient:#07ba55:#32f02b:#07ba55><newline>" +
                "<gradient:#d56cf5:#f59cff:#d56cf5>◆ 120 כוכבים ◆</gradient:#d56cf5:#f59cff:#d56cf5>"
        ), 170);
        DialogBody dailyQuest4 = DialogBody.item(ItemType.EMERALD.createItemStack()).description(dailyQuest4text).build();

        DialogBody seasonQuests = DialogBody.plainMessage(mm.deserialize("<u><gradient:#e37602:#ffd500:#e37602>משימות עונתיות</gradient:#e37602:#ffd500:#e37602></u>"), 400);


        List<DialogBody> bodies = new ArrayList<>();
        bodies.add(dailyQuests);
        bodies.add(dailyQuest1);
        bodies.add(dailyQuest2);
        bodies.add(dailyQuest3);
        bodies.add(dailyQuest4);
        bodies.add(seasonQuests);
        return bodies;
    }

    private DialogBody getQuestBlock(String questText, int required, int completed, String rewardText){
        List<String> builder = new ArrayList<>();

        MiniMessage mm = MiniMessage.miniMessage();
        builder.add( "<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>לקנות 1000 אייטמים מהחנות</gradient:#fabf1b:#faea3c:#fabf1b> ⏴<newline>");
    }
}

package me.stivendarsi.orbit.orbit.menus;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
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
        DialogBody seasonQuests = DialogBody.plainMessage(mm.deserialize("<u><gradient:#e37602:#ffd500:#e37602>משימות עונתיות</gradient:#e37602:#ffd500:#e37602></u>"), 400);


        List<DialogBody> bodies = new ArrayList<>();
        bodies.add(dailyQuests);
        bodies.add(getQuestBlock(ItemType.IRON_PICKAXE.createItemStack(), "לחצוב 200 בלוקים", 200, 43, "20 כוכבים", 120));
        bodies.add(getQuestBlock(ItemType.NETHERITE_SWORD.createItemStack(), "להרוג 500 מפלצות", 500, 500, "500 כוכבים", 120));
        bodies.add(seasonQuests);
        bodies.add(getQuestBlock(ItemType.IRON_PICKAXE.createItemStack(), "לחצוב 50000 בלוקים", 200, 43, "1000 כוכבים", 120));

        ItemStack enchanted = ItemType.NETHERITE_SWORD.createItemStack();
        enchanted.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, Boolean.TRUE);

        bodies.add(getQuestBlock(enchanted, "להרוג 100 שחקנים", 100, 100, "1000 כוכבים", 120));
        return bodies;
    }

    private DialogBody getQuestBlock(ItemStack itemStack, String questText, int required, int completed, String rewardText, int width) {
        List<String> builder = new ArrayList<>();

        boolean questCompleted = required <= completed;

        builder.add("<gray>⏵ <gradient:#fabf1b:#faea3c:#fabf1b>" + questText + "</gradient:#fabf1b:#faea3c:#fabf1b> ⏴</gray>");

        builder.add("<#fffb00>" + completed + "/" + required + "</#fffb00>");

        if (questCompleted)
            builder.add("<gradient:#07ba55:#32f02b:#07ba55>☑ הושלם</gradient:#07ba55:#32f02b:#07ba55>");
        else builder.add("<gradient:#e32a05:#ff4000:#e32a05>☐ עדיין לא הושלם</gradient:#e32a05:#ff4000:#e32a05>");

        builder.add("<gradient:#d56cf5:#f59cff:#d56cf5>◆ " + rewardText + " ◆</gradient:#d56cf5:#f59cff:#d56cf5>");

        Component text = MiniMessage.miniMessage().deserialize(String.join("<newline>", builder));
        return DialogBody.item(itemStack).description(DialogBody.plainMessage(text, width)).build();
    }
}

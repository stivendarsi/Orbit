package me.stivendarsi.orbit.orbit.menus;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

public class QuestMenu {
    public Dialog getQuestDialog(){
        Dialog questDialog = Dialog.create(b -> {
            b.empty().type(DialogType.notice()).base(DialogBase.builder(Component.text("משימות")).externalTitle(Component.text("משימות")).build());
        });

        return questDialog;
    }
}

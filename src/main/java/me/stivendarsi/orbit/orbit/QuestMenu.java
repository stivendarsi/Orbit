package me.stivendarsi.orbit.orbit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.type.DialogType;

public class QuestMenu {


    public Dialog getQuestDialog(){
        Dialog questDialog = Dialog.create(b -> {
            b.empty().type(DialogType.notice());
        });

        return questDialog;
    }
}

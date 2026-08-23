package me.stivendarsi.orbit.orbit.menus;

import com.google.common.base.Preconditions;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import me.stivendarsi.orbit.Constants;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.orbit.Orbit.mainHandler;

public class MainMenu {


    public static void openMainMenu(Player viewer) {
        Dialog dialog = Dialog.create(b -> {
            OrbitData currentOrbit = mainHandler().orbitHandler().getCurrentOrbit();
            Preconditions.checkNotNull(currentOrbit, "No current Orbit");

            OrbitMenu orbitMenu = new OrbitMenu(currentOrbit, viewer);
            QuestMenu questMenu = new QuestMenu(viewer);

            List<Dialog> dialogs = new ArrayList<>();

            dialogs.add(questMenu.getQuestDialog());
            dialogs.add(orbitMenu.getOrbitMenu());


            RegistrySet<Dialog> set = RegistrySet.valueSet(RegistryKey.DIALOG, dialogs);

            ActionButton exist = ActionButton.create(Constants.color(viewer, mainHandler().messagesHandler().getBackButton()), null, 100, null);

            DialogType type = DialogType.dialogList(set).exitAction(exist).build();

            Component orbitInfo = Constants.color(viewer, mainHandler().messagesHandler().getOrbitInfo());

            List<DialogBody> dialogBodies = new ArrayList<>();
            dialogBodies.add(DialogBody.plainMessage(orbitInfo, 200));

            b.empty().type(type).base(DialogBase.builder(Component.text("תפריט ראשי")).body(dialogBodies).build());
        });
        if (viewer != null) viewer.showDialog(dialog);
    }
}

package me.stivendarsi.orbit;

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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class OrbitMenu {
    private UUID user;
    private int userExperience;
    private final TreeMap<Integer, Integer> levelIndexRequiredExperienceMap; // level index , the amount of experience to unlock it.

    public OrbitMenu(UUID user, int userCurrentXp) {
        this.user = user;
        this.userExperience = userCurrentXp;
        this.levelIndexRequiredExperienceMap = new TreeMap<>();

        for (int levelIndex = 0; levelIndex < 100; levelIndex++) {
            levelIndexRequiredExperienceMap.put(levelIndex, levelIndex * 100);
        }
    }

    public void openOrbitMenu() {
        Player player = Bukkit.getPlayer(this.user);
        if (player == null) return;

        int levelIndex = getLevelIndex();

        int pageIndex = levelIndex % 10;

        player.showDialog(getOrbitPage(pageIndex));
    }

    private Dialog getOrbitPage(int pageIndex) {
        return Dialog.create(builder -> {
            List<DialogBody> dialogBodies = new ArrayList<>();
            dialogBodies.add(progressBar());

            DialogBase base = DialogBase.builder(Component.text("מסלול התקדמות")).body(dialogBodies).build();

            builder.empty().base(base).type(getOrbitButtons(pageIndex));

        });
    }

    public int getLevelIndex() {
        return this.levelIndexRequiredExperienceMap.floorKey(this.userExperience);
    }

    private MultiActionType getOrbitButtons(int pageIndex) {
        List<ActionButton> tiers = new ArrayList<>();

        int maxTier = 10 * pageIndex;
        int mimTier = 10 * (pageIndex - 1);

        int levelIndex = getLevelIndex();

        for (int index = mimTier; index < maxTier; index++) {
            Component text;

            if (levelIndex > index) text = Component.text("\uD83D\uDD13 " + levelIndex + 1);
            else text = Component.text("\uD83D\uDD12 " + levelIndex + 1);

            tiers.add(ActionButton.create(text, null, 35, null));
        }

        DialogAction previousPageAction = DialogAction.customClick((response, audience) -> {
            if (pageIndex - 1 <= 0) return;
            audience.showDialog(getOrbitPage(pageIndex - 1));
        }, ClickCallback.Options.builder().build());

        DialogAction nextPageAction = DialogAction.customClick((response, audience) -> {
            if (pageIndex + 1 > 10) return;
            audience.showDialog(getOrbitPage(pageIndex + 1));
        }, ClickCallback.Options.builder().build());

        tiers.add(ActionButton.create(Component.text("עמוד קודם"), null, 100, previousPageAction));
        tiers.add(ActionButton.create(Component.text("עמוד הבא"), null, 100, nextPageAction));

        return DialogType.multiAction(tiers).columns(10).build();
    }

    private DialogBody progressBar() {
        int level = getLevelIndex() + 1;
        int left = 100 - level;
        StringBuilder sb = new StringBuilder();

        sb.append("#ff85fd");

        sb.append("<gradient:#d6ffd1:#00ff48>");
        int shifts = 0;
        for (int i = 0; i < level; i++) {
            sb.append("‑");
            sb.append("<shift:-1>");
            shifts++;
        }

        if (left > 0) {
            sb.append("<dark_gray>");
            for (int i = 0; i < left; i++) {
                sb.append("‑");
                sb.append("<shift:-1>");
                shifts++;
            }
            sb.append("</gradient>");
        }
        System.out.println(shifts);

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(sb.toString()), 510);
    }
}

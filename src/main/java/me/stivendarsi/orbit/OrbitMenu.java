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
import java.util.UUID;

public class OrbitMenu {
    private UUID user;
    private int userExperience;
    private int currentLevelIndex;
    private final int[] levelRequiredExperience; // level index , the amount of experience to unlock it.

    public OrbitMenu(UUID user, int userCurrentXp) {
        this.user = user;
        this.userExperience = userCurrentXp;
        this.levelRequiredExperience = new int[100];

        for (int levelIndex = 0; levelIndex < levelRequiredExperience.length; levelIndex++) {
            this.levelRequiredExperience[levelIndex] = levelIndex * 100;
        }
        updateUserLevelIndex();
    }

    public void openOrbitMenu() {
        Player player = Bukkit.getPlayer(this.user);
        if (player == null) return;

        int pageIndex = currentLevelIndex % 10;

        player.showDialog(getOrbitPage(pageIndex));
    }

    private Dialog getOrbitPage(int pageIndex) {
        return Dialog.create(builder -> {
            List<DialogBody> dialogBodies = new ArrayList<>();

            dialogBodies.add(getExperienceLeft());
            dialogBodies.add(experienceProgressBar());

            DialogBase base = DialogBase.builder(Component.text("מסלול התקדמות")).body(dialogBodies).build();


            builder.empty().base(base).type(getOrbitButtons(pageIndex, false));

        });
    }

    private void updateUserLevelIndex() {
        for (int i = 0; i < levelRequiredExperience.length - 1; i++) {
            if (userExperience < levelRequiredExperience[i + 1]) {
                currentLevelIndex = i;
                return;
            }
        }

        currentLevelIndex = levelRequiredExperience.length - 1;
    }

    private int getNextLvlRequiredXp() {
        int index = this.currentLevelIndex + 1;
        return this.levelRequiredExperience.length <= index ? this.levelRequiredExperience[this.currentLevelIndex] : this.levelRequiredExperience[index];
    }

    private DialogBody getExperienceLeft() {
        int nextLvlRequiredXp = getNextLvlRequiredXp();
        return DialogBody.plainMessage(MiniMessage.miniMessage().deserialize("<#ff85fd>" + this.userExperience + " / " + nextLvlRequiredXp + "</#ff85fd>"));
    }

    private MultiActionType getOrbitButtons(int pageIndex, boolean orbitPlus) {
        List<ActionButton> tiers = new ArrayList<>();

        int minPageTierIndex = pageIndex * 10; // 1 * 10 = 10  2 * 10 = 20
        int maxPageTierIndex = (pageIndex + 1) * 10;

        for (int index = minPageTierIndex; index < maxPageTierIndex; index++) {
            Component text;

            if (this.currentLevelIndex >= index)
                text = Component.text("%s \uD83D\uDD13 ".formatted(index) + (index + 1)); // unlock
            else text = Component.text("\uD83D\uDD12 " + (index + 1)); // lock

            tiers.add(ActionButton.create(text, null, 35, null));
        }

        boolean positivePage = 0 < pageIndex;
        boolean allowedNextPage = pageIndex < 9;

        if (positivePage) tiers.add(ActionButton.create(Component.text("עמוד קודם"), null, 100, getPageButtonAction(pageIndex, false)));
        if (allowedNextPage) tiers.add(ActionButton.create(Component.text("עמוד הבא"), null, 100, getPageButtonAction(pageIndex, true)));

        return DialogType.multiAction(tiers).columns(10).build();
    }

    private DialogAction getPageButtonAction(int pageIndex, boolean nextPage) {
        return DialogAction.customClick((response, audience) -> {
            if (nextPage) audience.showDialog(getOrbitPage(pageIndex + 1));
            else audience.showDialog(getOrbitPage(pageIndex - 1));
        }, ClickCallback.Options.builder().build());
    }

    private DialogBody experienceProgressBar() {
        int nextXp = getNextLvlRequiredXp();
        int previousXp = this.levelRequiredExperience[this.currentLevelIndex];

        double progressPercent = nextXp == previousXp ? 100 : (double) (userExperience - previousXp) / (nextXp - previousXp) * 100;

        System.out.println("Current Level Index: " + this.currentLevelIndex);
        System.out.println("nextXp: " + nextXp);
        System.out.println("previousXp: " + previousXp);
        System.out.println("Progress: " + progressPercent);

        double remainingPercent = 100.0 - progressPercent;

        int currentLevel = this.currentLevelIndex + 1;


        StringBuilder sb = new StringBuilder();

        sb.append("<#ff85fd>").append(currentLevel).append(" </#ff85fd>");

        sb.append("<gradient:#d6ffd1:#00ff48>");


        sb.append("···");


        for (int i = 0; i <= progressPercent; i++) {
            sb.append("‑");
            sb.append("<shift:-1>");
        }

        if (remainingPercent > 0) {
            sb.append("<dark_gray>");
            for (int i = 0; i < remainingPercent; i++) {
                sb.append("‑");
                sb.append("<shift:-1>");
            }
        }
        sb.append("→</gradient>");


        sb.append("<#ff85fd> ").append(Math.min(100, currentLevel + 1)).append("</#ff85fd>");

        return DialogBody.plainMessage(MiniMessage.builder().tags(GlyphTag.INSTANCE.getRESOLVER()).build().deserialize(sb.toString()), 510);
    }
}

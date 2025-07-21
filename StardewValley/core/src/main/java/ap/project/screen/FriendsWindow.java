package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipData;
import ap.project.model.player_data.Skill;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.HashMap;
import java.util.Map;

public class FriendsWindow {
    private final Window popup;
    private final Stage stage;
    private final Table friendsTable;
    private final Skin skin;
    private boolean isVisible = false;
    private Player player;
    private TooltipManager tooltipManager = TooltipManager.getInstance();
    private Drawable tooltipBg;

    public FriendsWindow(Stage stage) {
        this.stage = stage;
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        popup = new Window("Friends", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        tooltipManager.initialTime = 0.5f; // Delay before tooltip shows
        tooltipManager.subsequentTime = 0.1f;
        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        friendsTable = buildFriendsTable();

        popup.add(friendsTable).colspan(4).expand().center(); popup.row();
        popup.pack();
        center(stage);
        stage.addActor(popup);

    }

    private Table buildFriendsTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        HashMap<Player, FriendshipData> friendships = player.getFriendships();

        for (Map.Entry<Player, FriendshipData> entry : friendships.entrySet()) {
            Player friend = entry.getKey();
            FriendshipData data = entry.getValue();

            // You can adjust this based on actual FriendshipData structure
            Label nameLabel = new Label("Name: " + friend.getNickName()
                + " | Level: " + data.getLevel(), skin);

            ProgressBar bar = new ProgressBar(0, data.getThresholdForLevel(data.getLevel()), 1, false, skin);
            bar.setValue(data.getXp());  // or whatever tracks friendship progress

            // Tooltip (optional)
            String tooltipText = friend.getNickName() + " (Level " + data.getLevel() + ")\n"
                + "XP: " + data.getXp() + "/" + data.getThresholdForLevel(data.getLevel());
            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().pad(8);
            nameLabel.addListener(tooltip);
            bar.addListener(tooltip);

            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(8);

            // Add to table
            table.add(nameLabel).left();
            table.add(bar).width(120);
            table.row();

            // Add tooltip to stage if needed
            stage.addActor(tooltip.getContainer());
        }

        return table;
    }


    private void center(Stage stage) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        popup.setPosition((w - popup.getWidth()) / 2f, (h - popup.getHeight()) / 2f);
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        popup.setVisible(isVisible);
        if (isVisible) buildFriendsTable();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }
}

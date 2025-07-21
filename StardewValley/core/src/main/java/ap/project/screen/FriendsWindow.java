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
    private TooltipManager tooltipManager = TooltipManager.getInstance();
    private Drawable tooltipBg;

    public FriendsWindow(Stage stage) {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        popup = new Window("Friends", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        tooltipManager.initialTime = 0.5f; // Delay before tooltip shows
        tooltipManager.subsequentTime = 0.1f;
        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        friendsTable = new Table();

        popup.setSize(700, 400); // Set fixed width and height
        friendsTable.setFillParent(true); // Make table expand inside popup

        ScrollPane scrollPane = new ScrollPane(friendsTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Allow vertical scroll

        popup.add(scrollPane).expand().fill().colspan(4);
        popup.row();

        center(stage);
        stage.addActor(popup);

    }

    private void refreshFriendsTable() {
        friendsTable.clearChildren(); // Clear old friend entries

        Player player = App.getCurrentGame().getCurrentPlayer();
        HashMap<Player, FriendshipData> friendships = player.getFriendships();

        for (Map.Entry<Player, FriendshipData> entry : friendships.entrySet()) {
            Player friend = entry.getKey();
            FriendshipData data = entry.getValue();

            Label nameLabel = new Label("Name: " + friend.getNickName()
                + " | Level: " + data.getLevel(), skin);

            ProgressBar bar = new ProgressBar(0, data.getThresholdForLevel(data.getLevel()), 1, false, skin);
            bar.setValue(data.getXp());

            String tooltipText = friend.getNickName() + " (Level " + data.getLevel() + ")\n"
                + "XP: " + data.getXp() + "/" + data.getThresholdForLevel(data.getLevel());
            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(8);
            nameLabel.addListener(tooltip);
            bar.addListener(tooltip);

            stage.addActor(tooltip.getContainer());

            friendsTable.add(nameLabel).left().padRight(30).padBottom(10);
            friendsTable.add(bar).width(120).padRight(30).padBottom(10);
            friendsTable.row();
        }

//        popup.pack(); // Resize popup to fit new content
        center(stage); // Recenter window
    }



    private void center(Stage stage) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        popup.setPosition((w - popup.getWidth()) / 2f, (h - popup.getHeight()) / 2f);
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        popup.setVisible(isVisible);
        if (isVisible) refreshFriendsTable();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }
}

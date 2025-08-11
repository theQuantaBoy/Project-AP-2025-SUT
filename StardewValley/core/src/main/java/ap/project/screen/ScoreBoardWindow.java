package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.Player;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreBoardWindow
{
    private final Window mainWindow;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;

    // Sorting state
    private String currentSortColumn = "username";
    private boolean isAscending = true;

    // UI Components
    private Label sortStatusLabel;
    private Table playerTable;

    // Constants
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 50;
    private static final float FONT_SCALE = 1.0f;
    private static final int ROW_HEIGHT = 60;
    private static final int PROGRESS_BAR_WIDTH = 120;

    public ScoreBoardWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        mainWindow = new Window("Score Board", skin);
        mainWindow.setMovable(true);
        mainWindow.add(buildContent()).pad(15);
        mainWindow.pack();
        mainWindow.setVisible(false);
        stage.addActor(mainWindow);
    }

    private Table buildContent()
    {
        Table content = new Table();
        content.defaults().pad(5).center();

        // Sort status label
        sortStatusLabel = new Label("Sorted by: Username - Ascending", skin);
        content.add(sortStatusLabel).colspan(4).padBottom(15).row();

        // Header buttons
        TextButton usernameBtn = createHeaderButton("Username", "username");
        TextButton goldBtn = createHeaderButton("Gold", "gold");
        TextButton questsBtn = createHeaderButton("Quests", "quests");
        TextButton skillBtn = createHeaderButton("Skill", "skill");

        Table headerRow = new Table();
        headerRow.add(usernameBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(5);
        headerRow.add(goldBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(5);
        headerRow.add(questsBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(5);
        headerRow.add(skillBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(5);

        content.add(headerRow).colspan(4).row();

        // Player table
        playerTable = new Table();
        playerTable.defaults().pad(5).height(ROW_HEIGHT);
        refreshPlayerTable();

        content.add(playerTable).colspan(4).row();

        // Close button
        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.getLabel().setFontScale(FONT_SCALE);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility();
            }
        });

        content.add(closeBtn).colspan(4).padTop(20).height(BUTTON_HEIGHT);

        return content;
    }

    private TextButton createHeaderButton(String label, String columnId)
    {
        TextButton button = new TextButton(label, skin);
        button.getLabel().setFontScale(FONT_SCALE);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                sortByColumn(columnId);
            }
        });
        return button;
    }

    private void sortByColumn(String columnId) {
        if (currentSortColumn.equals(columnId)) {
            // Toggle direction if same column
            isAscending = !isAscending;
        } else {
            // New column, default to ascending
            currentSortColumn = columnId;
            isAscending = true;
        }

        updateSortStatus();
        refreshPlayerTable();
    }

    private void updateSortStatus()
    {
        String direction = isAscending ? "Ascending" : "Descending";
        sortStatusLabel.setText("Sorted by: " + currentSortColumn + " - " + direction);
    }

    public void refreshPlayerTable() {
        playerTable.clearChildren();
        List<Player> players = App.getCurrentGame().getPlayers();

        // Sort players
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                int result = 0;
                switch (currentSortColumn) {
                    case "username":
                        result = p1.getUser().getUsername().compareTo(p2.getUser().getUsername());
                        break;
                    case "gold":
                        result = Double.compare(p1.getMoney(), p2.getMoney());
                        break;
                    case "quests":
                        result = Integer.compare(p1.getCompletedQuests(), p2.getCompletedQuests());
                        break;
                    case "skill":
                        result = Float.compare(p1.getSkillScore(), p2.getSkillScore());
                        break;
                }
                return isAscending ? result : -result;
            }
        });

        // Add player rows
        for (Player player : players) {
            // Username
            playerTable.add(new Label(player.getUser().getUsername(), skin))
                .width(BUTTON_WIDTH).height(ROW_HEIGHT).left();

            // Gold
            playerTable.add(new Label(String.format("%.2f", player.getMoney()), skin))
                .width(BUTTON_WIDTH).height(ROW_HEIGHT);

            // Completed Quests
            playerTable.add(new Label(String.valueOf(player.getCompletedQuests()), skin))
                .width(BUTTON_WIDTH).height(ROW_HEIGHT);

            // Skill Score (Progress Bar)
            ProgressBar bar = new ProgressBar(0, 1, 0.01f, false, skin);
            bar.setValue(player.getSkillScore());
            bar.setAnimateDuration(0); // Instant update

            // Tooltip
            String tooltipText = String.format("%s's Skill: %.0f%%",
                player.getUser().getUsername(), player.getSkillScore() * 100);
            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel);
            bar.addListener(tooltip);

            playerTable.add(bar).width(PROGRESS_BAR_WIDTH).height(ROW_HEIGHT);
            playerTable.row();
        }
    }

    public void refresh() {
        if (isVisible) {
            float x = mainWindow.getX();
            float y = mainWindow.getY();

            mainWindow.clear();
            mainWindow.add(buildContent()).pad(15);
            mainWindow.pack();
            mainWindow.setPosition(x, y);
        }
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        mainWindow.setVisible(isVisible);
        if (isVisible) {
            center();
            refreshPlayerTable();
        }
    }

    private void center() {
        mainWindow.setPosition(
            (stage.getWidth() - mainWindow.getWidth()) / 2,
            (stage.getHeight() - mainWindow.getHeight()) / 2
        );
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        mainWindow.remove();
    }

    public Window getWindow()
    {
        return mainWindow;
    }
}

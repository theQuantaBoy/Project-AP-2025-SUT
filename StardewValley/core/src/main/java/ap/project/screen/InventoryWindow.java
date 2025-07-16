package ap.project.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ap.project.model.App.GameAssetsManager;

/**
 * A popup inventory window with tabbed panels that can be shown/hidden.
 */
public class InventoryWindow {
    private Window popup;
    private Table inventoryTable;
    private Table skillsTable;
    private Table socialTable;
    private Table mapTable;
    private Stack contentStack;
    private boolean isVisible = false;
    Skin skin;

    public InventoryWindow(Stage stage) {
        skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Create popup window
        popup = new Window("Menu", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        // Tab buttons
        TextButton invTab = new TextButton("Inventory", skin);
        TextButton skillsTab = new TextButton("Skills", skin);
        TextButton socialTab = new TextButton("Social", skin);
        TextButton mapTab = new TextButton("Map", skin);

        // Build content tables
        inventoryTable = buildInventoryTable();
        skillsTable = buildSkillsTable();
        socialTable = buildSocialTable();
        mapTable = buildMapTable();

        // Stack the panels
        contentStack = new Stack();
        contentStack.add(inventoryTable);
        contentStack.add(skillsTable);
        contentStack.add(socialTable);
        contentStack.add(mapTable);
        skillsTable.setVisible(false);
        socialTable.setVisible(false);
        mapTable.setVisible(false);

        // Tab switching logic
        invTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(true);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        skillsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(true);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        socialTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(true);
                mapTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        mapTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(true);
                popup.pack();
                center(stage);
            }
        });

        // Assemble popup layout
        popup.row();
        popup.add(invTab).expandX().fillX();
        popup.add(skillsTab).expandX().fillX();
        popup.add(socialTab).expandX().fillX();
        popup.add(mapTab).expandX().fillX();
        popup.row();
        popup.add(contentStack).colspan(3).expand().fill();
        popup.pack();
        center(stage);

        stage.addActor(popup);
    }

    private Table buildInventoryTable() {
        Table table = new Table(skin);
        table.defaults().size(32).pad(2);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                table.add(new Button(skin));
            }
            table.row();
        }
        return table;
    }

    private Table buildSkillsTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        String[] skills = {"Farming", "Mining", "Foraging", "Fishing", "Combat"};
        for (String s : skills) {
            table.add(new Label(s, skin));
            ProgressBar bar = new ProgressBar(0, 100, 1, false, skin);
            bar.setValue((float) Math.random() * 100f);
            table.add(bar).width(120);
            table.row();
        }
        return table;
    }

    private Table buildSocialTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        for (int i = 1; i <= 10; i++) {
            table.add(new Label("Item #" + i, skin));
            table.add(new Label("x" + (int) (Math.random() * 50), skin));
            table.row();
        }
        return table;
    }
    private Table buildMapTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        for (int i = 1; i <= 10; i++) {
            table.add(new Label("Map #" + i, skin));
            table.add(new Label("x" + (int) (Math.random() * 50), skin));
            table.row();
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
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }
}

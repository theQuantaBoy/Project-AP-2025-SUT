package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A popup window with tabbed panels using a Stack.
 */
public class InventoryWindow implements Screen {
    private Stage stage;
    private Skin skin;
    private Window popup;
    private Table inventoryTable;
    private Table skillsTable;
    private Table shippingTable;
    private Stack contentStack;
    private boolean showing = false;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load a skin (ensure uiskin.json and atlas are in assets/ui/)
        skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Create popup window
        popup = new Window("Menu", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        // Tab buttons
        TextButton invTab    = new TextButton("Inventory", skin);
        TextButton skillsTab = new TextButton("Skills", skin);
        TextButton shipTab   = new TextButton("Shipping", skin);

        // Build content tables
        inventoryTable = buildInventoryTable();
        skillsTable    = buildSkillsTable();
        shippingTable  = buildShippingTable();

        // Stack the panels
        contentStack = new Stack();
        contentStack.add(inventoryTable);
        contentStack.add(skillsTable);
        contentStack.add(shippingTable);
        skillsTable.setVisible(false);
        shippingTable.setVisible(false);

        // Tab switching logic
        invTab.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(true);
                skillsTable.setVisible(false);
                shippingTable.setVisible(false);
                popup.pack(); center(popup);
            }
        });
        skillsTab.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(true);
                shippingTable.setVisible(false);
                popup.pack(); center(popup);
            }
        });
        shipTab.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(false);
                shippingTable.setVisible(true);
                popup.pack(); center(popup);
            }
        });

        // Assemble popup layout
        popup.row();
        popup.add(invTab).expandX().fillX();
        popup.add(skillsTab).expandX().fillX();
        popup.add(shipTab).expandX().fillX();
        popup.row();
        popup.add(contentStack).colspan(3).expand().fill();
        popup.pack();
        center(popup);

        stage.addActor(popup);
    }

    private Table buildInventoryTable() {
        Table table = new Table(skin);
        table.defaults().size(32).pad(2);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                // Placeholder cell: simple button instead of missing drawable
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
            table.add(new com.badlogic.gdx.scenes.scene2d.ui.Label(s, skin));
            ProgressBar bar = new ProgressBar(0, 100, 1, false, skin);
            bar.setValue((float)Math.random() * 100f);
            table.add(bar).width(120);
            table.row();
        }
        return table;
    }

    private Table buildShippingTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        // Example shipped items list
        for (int i = 1; i <= 10; i++) {
            table.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Item #" + i, skin));
            table.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("x" + (int)(Math.random()*50), skin));
            table.row();
        }
        return table;
    }

    private void center(Window window) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth())/2f, (h - window.getHeight())/2f);
    }

    @Override
    public void render(float delta) {
        // Toggle popup with I
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showing = !showing;
            popup.setVisible(showing);
            if (showing) center(popup);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        center(popup);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}

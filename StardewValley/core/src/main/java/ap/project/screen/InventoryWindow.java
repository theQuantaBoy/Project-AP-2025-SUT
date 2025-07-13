package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A simplified pop-up inventory window without custom slot drawables.
 */
public class InventoryWindow extends Window {
    private static final int COLUMNS = 8;
    private static final int ROWS = 4;

    public InventoryWindow(Skin skin) {
        super("Inventory", skin);
        Table grid = new Table();
        grid.defaults().size(48).pad(4);

        // Create a grid of empty buttons as placeholders
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                Button cell = new Button(skin);
                grid.add(cell);
            }
            grid.row();
        }

        this.add(grid).pad(8);
        this.pack();
        this.setMovable(true);
    }

    /**
     * Toggle visibility and center on stage when shown.
     */
    public void toggle(Stage stage) {
        boolean visible = !isVisible();
        setVisible(visible);
        if (visible) {
            centerOnStage(stage);
        }
    }

    private void centerOnStage(Stage stage) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        setPosition((w - getWidth()) / 2f, (h - getHeight()) / 2f);
    }

    // Sample GameScreen integration
    public static class GameScreen implements Screen {
        private Stage stage;
        private InventoryWindow inventory;
        private Viewport viewport;

        @Override
        public void show() {
            viewport = new ScreenViewport();
            stage = new Stage(viewport);

            // Load skin via your assets manager
            Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

            inventory = new InventoryWindow(skin);
            inventory.setVisible(false);
            stage.addActor(inventory);

            InputMultiplexer im = new InputMultiplexer();
            im.addProcessor(stage);
            Gdx.input.setInputProcessor(im);
        }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                inventory.toggle(stage);
            }

            stage.act(delta);
            stage.draw();
        }

        @Override public void resize(int width, int height) {
            stage.getViewport().update(width, height, true);
        }
        @Override public void pause() {}
        @Override public void resume() {}
        @Override public void hide() {}
        @Override public void dispose() { stage.dispose(); }
    }
}


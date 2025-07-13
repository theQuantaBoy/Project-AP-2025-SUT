package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TempScreen implements Screen {
    private Stage stage;
    private Image background;
    private Image logo;
    private InventoryWindow inventoryWindow;
    private boolean showInventory = false;
    private Viewport viewport;


    public TempScreen() {
        this.viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
        Gdx.input.setInputProcessor(this.stage);


        // Load assets
        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        positionElements();
        inventoryWindow = new InventoryWindow(GameAssetsManager.getGameAssetsManager().getSkin());
        inventoryWindow.setVisible(false);
        inventoryWindow.pack();
        inventoryWindow.setPosition(
            (viewport.getWorldWidth()  - inventoryWindow.getWidth())  / 2,
            (viewport.getWorldHeight() - inventoryWindow.getHeight()) / 2
        );
        stage.addActor(inventoryWindow);

        InputMultiplexer imux = new InputMultiplexer();
        imux.addProcessor(stage);
        //imux.addProcessor(worldInputProcessor); // your game controls
        Gdx.input.setInputProcessor(imux);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1/30f));
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showInventory = !showInventory;
            inventoryWindow.setVisible(showInventory);
            // optionally: pause your world update when inventory is open
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        positionElements();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void positionElements() {
        // Position logo at top center
        float logoCenterX = stage.getWidth() / 2 - logo.getWidth() / 2;
        float logoTopY = stage.getHeight() - logo.getHeight() - 20;
        logo.setPosition(logoCenterX, logoTopY);

//        // Position label at top left
//        float labelLeftX = 20;
//        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
//        menuName.setPosition(labelLeftX, labelTopY);
//
//
//        float tableWidth = table.getPrefWidth();
//        float tableHeight = table.getPrefHeight();
//        float x = (stage.getWidth())  / 2;
//        float y = (stage.getHeight()) - tableHeight/2 - 140;
//        table.setPosition(x, y);

    }
}

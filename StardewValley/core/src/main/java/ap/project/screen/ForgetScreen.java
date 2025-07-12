package ap.project.screen;

import ap.project.control.MainMenuController;
import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ForgetScreen implements Screen {
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private Table table;

    public ForgetScreen() {
        stage =  new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("FORGET PASSWORD\nMENU", GameAssetsManager.getGameAssetsManager().getSkin());
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);

        this.table = new Table();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(table);
        positionElements();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1/30f));
        stage.draw();
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

        // Position label at top left
        float labelLeftX = 20;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);


        float tableWidth = table.getPrefWidth();
        float tableHeight = table.getPrefHeight();
        float x = (stage.getWidth())  / 2;
        float y = (stage.getHeight()) / 2;
        table.setPosition(x, y);

    }

}

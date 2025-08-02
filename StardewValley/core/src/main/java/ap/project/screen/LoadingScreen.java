package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import ap.project.network.client.GameClient;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoadingScreen implements Screen
{
    private final Stage stage = new Stage(new ScreenViewport());
    private final Label loadingLabel;

    public LoadingScreen()
    {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
        loadingLabel = new Label("Waiting for players...", skin);
        loadingLabel.setPosition(
            Gdx.graphics.getWidth()/2 - loadingLabel.getWidth()/2,
            Gdx.graphics.getHeight()/2
        );
        stage.addActor(loadingLabel);
    }

    @Override
    public void render(float delta)
    {
        // Process network messages
        GameClient.getInstance().processMessages();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show()
    {

    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }
}

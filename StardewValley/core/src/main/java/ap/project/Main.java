package ap.project;

import ap.project.screen.TerminalScreen;
import ap.project.view.AppView;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends com.badlogic.gdx.Game
{
    private SpriteBatch batch;
    private Texture image;
    private static Main app;

    @Override
    public void create() {
//        batch = new SpriteBatch();
//        image = new Texture("libgdx.png");

        app = this;
        TerminalScreen.run(); // start first input
        setScreen(TerminalScreen.getInstance());
        getApp().setScreen(new TerminalScreen());
    }

    @Override
    public void render()
    {
        super.render();

        if (TerminalScreen.isSubmitted())
        {
            TerminalScreen.reset();
            TerminalScreen.run();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    public static Main getApp()
    {
        return app;
    }

    public void runConsole()
    {
        // 👇 Start a dummy LibGDX backend just for file access
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {}, config);  // doesn't launch GUI

        (new AppView()).runInConsole();
    }
}

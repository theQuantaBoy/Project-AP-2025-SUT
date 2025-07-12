package ap.project;

import ap.project.control.MainMenuController;
import ap.project.control.RegisterController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.screen.MainScreen;
import ap.project.screen.RegisterScreen;
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
    private static Main app;
    private SpriteBatch batch;

    @Override
    public void create()
    {
        app = this;
        batch = new SpriteBatch();
//        TerminalScreen.run(); // start first input
//        setScreen(TerminalScreen.getInstance());
//        getApp().setScreen(new TerminalScreen());
        User newUser = new User("arash", "a36213126A@", "arash", "arash@gmail.com", Gender.MALE, "what is your favorite animal", "cat");
        App.getUsers().add(newUser);
        App.setCurrentUser(newUser);
        app.setScreen(new MainScreen(new MainMenuController()));
        //app.setScreen(new RegisterScreen(new RegisterController()));
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
    public void dispose()
    {
       super.dispose();
    }

    public static Main getApp()
    {
        return app;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void runConsole()
    {
        // 👇 Start a dummy LibGDX backend just for file access
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {}, config);  // doesn't launch GUI

        (new AppView()).runInConsole();
    }
}

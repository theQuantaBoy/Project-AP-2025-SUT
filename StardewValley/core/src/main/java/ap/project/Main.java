package ap.project;

import ap.project.control.MainMenuController;
import ap.project.control.RegisterController;
import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.SecurityQuestionType;
import ap.project.screen.MainScreen;
import ap.project.screen.RegisterScreen;
import ap.project.screen.TerminalScreen;
import ap.project.screen.WorldScreen;
import ap.project.view.AppView;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

        // --- Set custom cursor ---
        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("general/cursor/cursor.png")); // put it in assets
        int hotspotX = 0; // adjust to your cursor's "click point"
        int hotspotY = 0;
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorPixmap, hotspotX, hotspotY));
        cursorPixmap.dispose();

//        User newUser = new User("arash", "a36213126A@", "arash", "arash@gmail.com", Gender.MALE, SecurityQuestionType.ANIMAL.getQuestion(), "cat");
//        App.setCurrentUser(newUser);
//        app.setScreen(new MainScreen(new MainMenuController()));
//        app.setScreen(new RegisterScreen(new RegisterController()));

        app.setScreen(new WorldScreen());
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

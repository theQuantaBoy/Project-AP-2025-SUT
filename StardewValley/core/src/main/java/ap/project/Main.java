package ap.project;

import ap.project.control.MainMenuController;
import ap.project.control.RegisterController;
import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.SecurityQuestionType;
import ap.project.model.game.Game;
import ap.project.model.game.Player;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.GameConfigMessage;
import ap.project.network.shared.messages.TestMessage;
import ap.project.network.shared.messages.UserProfileMessage;
import ap.project.screen.*;
import ap.project.screen.MainScreen;
import ap.project.screen.RegisterScreen;
import ap.project.screen.TerminalScreen;
import ap.project.screen.WorldScreen;
import ap.project.util.GameObjectAssetLoader;
import ap.project.view.AppView;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import java.util.ArrayList;
import java.util.Scanner;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends com.badlogic.gdx.Game
{
    private static Main instance;
    private static Main app;
    private SpriteBatch batch;

    @Override
    public void create()
    {
        instance = this;
        app = this;
        batch = new SpriteBatch();

        GameObjectAssetLoader.queueAllTextures();
        // optionally show a loading screen here...
        GameObjectAssetLoader.finishLoadingAndAssign();

        // --- Set custom cursor ---
        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("general/cursor/cursor.png")); // put it in assets
        int hotspotX = 0; // adjust to your cursor's "click point"
        int hotspotY = 0;
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorPixmap, hotspotX, hotspotY));
        cursorPixmap.dispose();

        // Connect to server FIRST
        GameClient.getInstance().connect("127.0.0.1");

        // Create test user profile (hardcoded for testing)
        String name = "player" + (int)(Math.random() * 1000);
        String nickname = "Player" + (int)(Math.random() * 1000);

        // Set loading screen immediately
        setScreen(new LoadingScreen());

        // Send profile after short delay (ensure connection completes)
        new Thread(() -> {
            try {
                // Wait for connection
                int attempts = 0;
                while (!GameClient.getInstance().isConnected() && attempts < 50) {
                    Thread.sleep(100);
                    attempts++;
                }

                if (GameClient.getInstance().isConnected()) {
                    UserProfileMessage profile = new UserProfileMessage(
                        name, nickname, Gender.MALE, 1
                    );
                    GameClient.getInstance().send(profile);
                    System.out.println("Sent profile: " + name);
                } else {
                    System.out.println("Failed to connect to server");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

//        app.setScreen(new WorldScreen());
    }

    public void onGameConfigReceived(GameConfigMessage config)
    {
        // Create players from config
        ArrayList<Player> players = new ArrayList<>();
        for (GameConfigMessage.PlayerConfig pc : config.players)
        {
            User user = new User(
                pc.username,
                "",  // No password needed
                pc.nickname,
                "",  // No email
                Gender.MALE,  // Default gender
                "",  // No security Q
                ""   // No security A
            );
            Player player = new Player(user, pc.mapType, pc.playerIndex);
            players.add(player);
        }

        // Create and set game
        Game game = new Game(players);
        App.setCurrentGame(game);
        game.setCurrentPlayer(game.getPlayers().get(config.yourPlayerIndex));

        // Start world screen
        setScreen(new WorldScreen(true));
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
       GameObjectAssetLoader.dispose();
    }

    public static Main getApp()
    {
        return app;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}

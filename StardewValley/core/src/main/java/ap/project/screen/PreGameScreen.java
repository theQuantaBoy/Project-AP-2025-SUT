package ap.project.screen;

import ap.project.Main;
import ap.project.control.MainMenuController;
import ap.project.control.PreGameController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.UserProfileMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static java.lang.Thread.sleep;

public class PreGameScreen implements Screen
{
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private TextButton playOffline;
    private TextButton playOnline;
    private TextButton backButton;
    private Dialog connectionDialog;

    public PreGameScreen()
    {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(this.stage);

        // Load assets - same as RegisterScreen
        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("Choose how to play", GameAssetsManager.getGameAssetsManager().getSkin(), "Impact");
        this.menuName.setAlignment(Align.center);

        // Create buttons with same style
        this.playOffline = new TextButton("Play Offline", GameAssetsManager.getGameAssetsManager().getSkin());
        this.playOnline = new TextButton("Play Online", GameAssetsManager.getGameAssetsManager().getSkin());
        this.backButton = new TextButton("Back", GameAssetsManager.getGameAssetsManager().getSkin());

        // Build layout with centered content
        Table contentTable = new Table();
        contentTable.add(playOffline).width(350).height(75).pad(10).row();
        contentTable.add(playOnline).width(350).height(75).pad(10).row();

        // Create container to center the content
        Container<Table> container = new Container<>(contentTable);
        container.setFillParent(true);
        container.center();

        // Create bottom table for back button
        Table bottomTable = new Table();
        bottomTable.bottom().right();
        bottomTable.setFillParent(true);
        bottomTable.add(backButton).pad(20).size(200, 75);

        // Add button listeners
        addButtonListeners();

        // Add actors to stage
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(container);
        stage.addActor(bottomTable);

        // Add button listeners
        addButtonListeners();
    }

    private void addButtonListeners()
    {
        playOffline.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Play Offline selected");
                Main.getApp().setScreen(new OfflinePreGameScreen(new PreGameController()));
            }
        });

        playOnline.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                GameClient client = GameClient.getInstance();
                startClient();

                if (!client.isConnected() || !client.isRegistered())
                {
                    showConnectionError();
                } else
                {
                    Main.getApp().setScreen(new PreLobbyScreen());
                }
            }
        });

        // Back button listener
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getApp().setScreen(new MainScreen(new MainMenuController()));
            }
        });
    }

    private void showConnectionError()
    {
        if (connectionDialog != null) return;

        connectionDialog = new Dialog("Connection Failed", GameAssetsManager.getGameAssetsManager().getSkin());
        connectionDialog.text("Could not connect to server");

        TextButton tryAgainButton = new TextButton("Try Again", GameAssetsManager.getGameAssetsManager().getSkin());
        tryAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                connectionDialog.hide();
                connectionDialog = null;
                startClient();
            }
        });

        TextButton goBackButton = new TextButton("Go Back", GameAssetsManager.getGameAssetsManager().getSkin());
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                connectionDialog.hide();
                connectionDialog = null;
            }
        });

        connectionDialog.button(tryAgainButton);
        connectionDialog.button(goBackButton);
        connectionDialog.show(stage);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        positionElements();
    }

    private void positionElements()
    {
        // Position logo at top center
        float logoCenterX = stage.getWidth() / 2 - logo.getWidth() / 2;
        float logoTopY = stage.getHeight() - logo.getHeight() - 20;
        logo.setPosition(logoCenterX, logoTopY);

        // Position label at top left
        float labelLeftX = 20;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1/30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
        positionElements();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose()
    {
        if (stage != null) {
            stage.dispose();
        }
    }

    public void startClient()
    {
        try
        {
            User currentUser = App.getCurrentUser();
            if (currentUser == null)
            {
                System.err.println("No current user available");
                return;
            }

            GameClient client = GameClient.getInstance();
            client.connect();

            // Wait for connection to establish
            int attempts = 0;
            while (!client.isConnected() && attempts < 50)
            {
                sleep(100);
                attempts++;
            }

            if (client.isConnected())
            {
                client.requestUserSync();

                // Wait for user sync to complete
                attempts = 0;
                while (!client.isUserSyncComplete() && attempts < 50)
                {
                    client.processMessages();
                    sleep(100);
                    attempts++;
                }

                if (!client.isUserSyncComplete())
                {
                    System.err.println("User sync timed out");
                    return;
                }

                // 2. Send user update to ensure server has latest version
                client.sendUserUpdate(currentUser);

                // 3. Send minimal profile message with just ID
                UserProfileMessage message = new UserProfileMessage(currentUser.getHashId());
                client.send(message);

                // 4. Wait for registration confirmation
                attempts = 0;
                while (!client.isRegistered() && attempts < 50)
                {
                    client.processMessages();
                    sleep(100);
                    attempts++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

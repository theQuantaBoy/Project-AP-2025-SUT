package ap.project.screen;

import ap.project.Main;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
    private Table table;
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

        this.table = new Table();

        // Create buttons with same style
        this.playOffline = new TextButton("Play Offline", GameAssetsManager.getGameAssetsManager().getSkin());
        this.playOnline = new TextButton("Play Online", GameAssetsManager.getGameAssetsManager().getSkin());

        // Build layout
        table.add(playOffline).width(500).height(55).pad(20).row();
        table.add(playOnline).width(500).height(55).pad(20).row();

        // Add button listeners
        addButtonListeners();
    }

    private void addButtonListeners() {
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
                    Main.getApp().setScreen(new WorldScreen());
                }
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(table);
        positionElements();
    }

    private void positionElements() {
        // Position logo at top center - same as RegisterScreen
        float logoCenterX = stage.getWidth() / 2 - logo.getWidth() / 2;
        float logoTopY = stage.getHeight() - logo.getHeight() - 20;
        logo.setPosition(logoCenterX, logoTopY);

        // Position label at top left - same as RegisterScreen
        float labelLeftX = 20;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);

        // Center table vertically - adjusted for this screen's simplicity
        float tableWidth = table.getPrefWidth();
        float tableHeight = table.getPrefHeight();
        float x = stage.getWidth() / 2 - tableWidth / 2;
        float y = stage.getHeight() / 2 - tableHeight / 2 + 50; // Slightly higher than center
        table.setPosition(x, y);
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }

    public void startClient()
    {
        try {
            User currentUser = App.getCurrentUser();
            if (currentUser == null) {
                System.err.println("No current user available");
                return;
            }

            GameClient client = GameClient.getInstance();
            client.connect("localhost");

            // Wait for connection to establish
            int attempts = 0;
            while (!client.isConnected() && attempts < 50) {
                sleep(100);
                attempts++;
            }

            if (client.isConnected()) {
                UserProfileMessage message = new UserProfileMessage(currentUser.getUsername(),
                    currentUser.getNickname(), currentUser.getGender().toString(), currentUser.getHashId());
                client.send(message);

                // Process messages and wait for response
                attempts = 0;
                while (!client.isRegistered() && attempts < 50) {
                    client.processMessages();  // Process any incoming messages
                    sleep(100);
                    attempts++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

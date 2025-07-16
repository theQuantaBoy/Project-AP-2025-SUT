package ap.project.screen;

import ap.project.Main;
import ap.project.control.LoginController;
import ap.project.control.MainMenuController;
import ap.project.control.PreGameController;
import ap.project.control.ProfileController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class PreGameScreen implements Screen
{
    private PreGameController controller;
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private Label nickname;
    private Table table;
    TextField username;
    private TextButton addPlayerButton;
    private TextButton newGameButton;
    private TextButton backButton;
    private TextButton exitButton;

    private ArrayList<User> users;

    public PreGameScreen(PreGameController controller)
    {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.controller = controller;

        users = new ArrayList<>();
        users.add(App.getCurrentUser());

        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("NEW\nGAME", GameAssetsManager.getGameAssetsManager().getSkin());
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);
        this.nickname = new Label(App.getCurrentUser().getNickname(), GameAssetsManager.getGameAssetsManager().getSkin());
        this.nickname.setAlignment(Align.center);
        this.nickname.setColor(Color.GOLD);
        this.table = new Table();

        this.username = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.username.setMessageText("Username");
        this.username.setAlignment(Align.center);

        this.addPlayerButton = new TextButton("Add Player", GameAssetsManager.getGameAssetsManager().getSkin());
        this.newGameButton = new TextButton("Create Game", GameAssetsManager.getGameAssetsManager().getSkin());
        this.backButton = new TextButton("Back", GameAssetsManager.getGameAssetsManager().getSkin());
        this.exitButton = new TextButton("Exit", GameAssetsManager.getGameAssetsManager().getSkin());

        table.add(username).width(500).height(50).pad(10).row();
        table.add(addPlayerButton).width(500).height(50).pad(10).row();
        table.add(newGameButton).width(500).height(50).pad(10).row();
        table.add(backButton).width(500).height(50).pad(10).row();
        table.add(exitButton).width(500).height(50).pad(10).row();

        addButtonListeners();
    }

    private void addButtonListeners() {
        addPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                User user = controller.getUser(username.getText());
                if (user != null) {
                    users.add(user);
                }
            }
        });

        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String[] usernames = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    usernames[i] = users.get(i).getUsername();
                }
                controller.newGame(usernames);
                Main.getApp().setScreen(new TestScreen());
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new MainScreen(new MainMenuController()));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show()
    {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(nickname);
        stage.addActor(table);
        positionElements();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
        positionElements();
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

    private void positionElements()
    {
        // Position logo at top center
        float logoCenterX = stage.getWidth() / 2 - logo.getWidth() / 2;
        float logoTopY = stage.getHeight() - logo.getHeight() - 20;
        logo.setPosition(logoCenterX, logoTopY);

        // Position label at top left
        float labelLeftX = 20;
        float labelRightX = stage.getWidth() - nickname.getPrefWidth() - 100;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);
        nickname.setPosition(labelRightX, labelTopY);


        float tableWidth = table.getPrefWidth();
        float tableHeight = table.getPrefHeight();
        float x = (stage.getWidth()) / 2;
        float y = (stage.getHeight()) / 2;
        table.setPosition(x, y);
    }
}

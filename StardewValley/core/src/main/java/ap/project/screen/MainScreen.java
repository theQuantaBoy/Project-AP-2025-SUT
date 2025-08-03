package ap.project.screen;

import ap.project.Main;
import ap.project.control.LoginController;
import ap.project.control.MainMenuController;
import ap.project.control.PreGameController;
import ap.project.control.ProfileController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainScreen implements Screen {
    private Stage stage;
    private MainMenuController controller;
    private Image background;
    private Image logo;
    private Image avatar;
    private Label menuName;
    private Label nickname;
    private Table table;
    private TextButton preGameButton;
    private TextButton profileButton;
    private TextButton logoutButton;
    private TextButton exit;


    public MainScreen(MainMenuController controller) {
        stage =  new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.controller = controller;

        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.avatar = new Image(App.getCurrentUser().getAvatar());
        this.menuName = new Label("MAIN\nMENU", GameAssetsManager.getGameAssetsManager().getSkin(), "Impact");
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);
        this.nickname = new Label(App.getCurrentUser().getNickname(), GameAssetsManager.getGameAssetsManager().getSkin());
        this.nickname.setAlignment(Align.center);
        this.nickname.setColor(Color.GOLD);
        this.table = new Table();

        this.preGameButton = new TextButton("Pre Game", GameAssetsManager.getGameAssetsManager().getSkin());
        this.profileButton = new TextButton("Profile", GameAssetsManager.getGameAssetsManager().getSkin());
        this.logoutButton = new TextButton("Logout", GameAssetsManager.getGameAssetsManager().getSkin());
        this.exit = new TextButton("Exit", GameAssetsManager.getGameAssetsManager().getSkin());

        table.add(preGameButton).width(500).height(50).pad(10).row();
        table.add(profileButton).width(500).height(50).pad(10).row();
        table.add(logoutButton).width(500).height(50).pad(10).row();
        table.add(exit).width(500).height(50).pad(10).row();

        addButtonListeners();
    }

    private void addButtonListeners() {
        // Enter button listener
        preGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new PreGameScreen());
            }
        });

        // Random password button listener
        profileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new ProfileMenu(new ProfileController()));
            }
        });

        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.logout();
                Main.getApp().setScreen(new LoginScreen(new LoginController()));
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(nickname);
        stage.addActor(table);
        stage.addActor(avatar);
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
        float labelRightX = stage.getWidth() - nickname.getPrefWidth() - 100;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);
        avatar.setPosition(labelLeftX + 5, labelTopY - avatar.getHeight() - 40);
        nickname.setPosition(labelRightX, labelTopY);


        float tableWidth = table.getPrefWidth();
        float tableHeight = table.getPrefHeight();
        float x = (stage.getWidth())  / 2;
        float y = (stage.getHeight()) / 2;
        table.setPosition(x, y);
    }

}

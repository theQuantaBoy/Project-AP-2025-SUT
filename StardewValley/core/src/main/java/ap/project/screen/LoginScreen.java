package ap.project.screen;

import ap.project.Main;
import ap.project.control.LoginController;
import ap.project.control.MainMenuController;
import ap.project.control.RegisterController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
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

public class LoginScreen implements Screen {
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private LoginController controller;
    private TextField username;
    private TextField password;
    private TextButton enter;
    private CheckBox stayLoggedIn;
    private TextButton signup;
    private TextButton forgotPassword;
    private TextButton exit;
    private Label errorLabel;
    private Table table;

    public LoginScreen(LoginController controller) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.controller = controller;

        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("LOGIN\nMENU", GameAssetsManager.getGameAssetsManager().getSkin(), "Impact");
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);

        this.table = new Table();


        this.username = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.username.setMessageText("Username");
        this.username.setAlignment(Align.center);

        User u = App.getLoggedInUser();
        if (u != null)
        {
            this.username.setText(u.getUsername());
        }

        this.password = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.password.setMessageText("Password");
        this.password.setAlignment(Align.center);
        this.password.setPasswordMode(true);
        this.password.setPasswordCharacter('*');

        this.enter = new TextButton("Login", GameAssetsManager.getGameAssetsManager().getSkin());
        this.stayLoggedIn = new CheckBox("stay logged in", GameAssetsManager.getGameAssetsManager().getSkin());
        this.forgotPassword =  new TextButton("Forgot Password", GameAssetsManager.getGameAssetsManager().getSkin());
        this.signup = new TextButton("Sign up", GameAssetsManager.getGameAssetsManager().getSkin());
        this.exit = new TextButton("Exit", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel = new Label("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel.setAlignment(Align.center);
        this.errorLabel.setColor(Color.RED);

        table.add(username).width(500).height(50).pad(10).row();
        table.add(password).width(500).height(50).pad(10).row();
        Table table1 = new Table();
        table1.add(enter).width(220).height(50).pad(3);
        table1.add(stayLoggedIn).width(280).height(50).pad(3).row();
        table.add(table1).row();
        table.add(forgotPassword).width(500).height(50).pad(10).row();
        table.add(signup).width(500).height(50).pad(10).row();
        table.add(exit).width(500).height(50).pad(10).row();
        table.add(errorLabel).width(500).height(50).pad(10).row();

        addButtonListeners();
    }

    private void addButtonListeners() {
        // Enter button listener
        enter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user = username.getText();
                String pass = password.getText();
                boolean stayLoggedIn1 = stayLoggedIn.isChecked();


                // Call controller to handle registration
                Result result = controller.login(user, pass, stayLoggedIn1);
                errorLabel.setText(result.message());
                if (result.isSuccessful()) {
                    Main.getApp().setScreen(new MainScreen(new MainMenuController()));
                }
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        signup.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new RegisterScreen(new RegisterController()));
            }
        });

        forgotPassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (username.getText().equals("") || username.getText().equals("Username")) {
                    errorLabel.setText("Please enter username");
                }
                User user = App.getPlayerByUsername(username.getText());
                if (user == null) {
                    errorLabel.setText("Invalid username");
                } else {
                    Main.getApp().setScreen(new ForgetScreen(user));
                }
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

package ap.project.screen;

import ap.project.control.RegisterController;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RegisterScreen implements Screen {
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField email;
    private TextField nickname;
    private SelectBox<String> gender;
    private TextButton enter;
    private TextButton randomPass;
    private Label errorLabel;
    private Table table;
    private RegisterController controller;

    public RegisterScreen(RegisterController controller) {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(this.stage);
        this.controller = controller;

        // Load assets
        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("REGISTRATION\nMENU", GameAssetsManager.getGameAssetsManager().getSkin());
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);

        this.table = new Table();


        this.username = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.username.setMessageText("Username");
        this.username.setAlignment(Align.center);

        this.password = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.password.setMessageText("Password");
        this.password.setAlignment(Align.center);
        this.password.setPasswordMode(true);
        this.password.setPasswordCharacter('*');

        this.confirmPassword = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.confirmPassword.setMessageText("Confirm");
        this.confirmPassword.setAlignment(Align.center);
        this.confirmPassword.setPasswordMode(true);
        this.confirmPassword.setPasswordCharacter('*');

        this.email = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.email.setMessageText("Email");
        this.email.setAlignment(Align.center);

        this.nickname = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.nickname.setMessageText("Nickname");
        this.nickname.setAlignment(Align.center);

        // Initialize gender select box

        Array<String> genderOptions = new Array<>(); genderOptions.add("Male"); genderOptions.add("Female");
        this.gender = new SelectBox<>(GameAssetsManager.getGameAssetsManager().getSkin());
        this.gender.setItems(genderOptions);
        this.enter = new TextButton("Create Account", GameAssetsManager.getGameAssetsManager().getSkin());
        this.randomPass = new TextButton("rand", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel = new Label("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel.setAlignment(Align.center);
        this.errorLabel.setColor(Color.RED);
        this.errorLabel.setFontScale(2);


        // Build form layout
        table.add(username).width(500).height(50).pad(10).row();

        Table passwordRow = new Table();
        table.add(password).width(500).height(50).pad(10).row();
        passwordRow.add(confirmPassword).width(300).height(50).pad(10);
        passwordRow.add(randomPass).width(180).height(50).pad(10).row();
        table.add(passwordRow).row();
        table.add(email).width(500).height(50).pad(10).row();
        table.add(nickname).width(500).height(50).pad(10).row();
        table.add(gender).width(500).height(50).pad(10).row();
        table.add(enter).width(300).height(60).pad(20).row();
        table.add(errorLabel).width(300).height(60).pad(20).row();

        // Add button listeners
        addButtonListeners();
    }

    private void addButtonListeners() {
        // Enter button listener
        enter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user = username.getText();
                String pass = password.getText();
                String confirmPass = confirmPassword.getText();
                String emailText = email.getText();
                String nick = nickname.getText();
                String selectedGender = gender.getSelected();
                Gender genderEnum = Gender.valueOf(selectedGender.toUpperCase());


                // Call controller to handle registration
                Result result = controller.register(user, pass, confirmPass, emailText, nick, genderEnum);
                errorLabel.setText(result.message());
            }
        });

        // Random password button listener
        randomPass.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String generatedPassword = generateRandomPassword(12);
                password.setText(generatedPassword);
                confirmPassword.setText(generatedPassword);
            }
        });
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
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
}
